const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');

const html = fs.readFileSync(path.join(__dirname, '../../main/resources/static/index.html'), 'utf8');
const script = [...html.matchAll(/<script>([\s\S]*?)<\/script>/g)].at(-1)[1];
const response = (status, csrfToken = 'csrf-a', revision = 1) => ({
    status, ok: status >= 200 && status < 300,
    json: async () => ({ html: '<p>rendered</p>', htmlDependencies: {}, fullPage: false, csrfToken, revision })
});
const tick = () => new Promise(resolve => setImmediate(resolve));

function browser(fetch, { navigation = 'reload', storedView = 'tab-a' } = {}) {
    const storage = new Map(storedView ? [['jui-view-id', storedView]] : []);
    const nodes = new Map();
    const node = () => ({
        innerHTML: '', textContent: '', innerText: '', scrollHeight: 0,
        classList: { add() {}, remove() {}, contains() { return false; }, toggle() { return false; } },
        appendChild() {}, querySelectorAll() { return []; }, contains() { return false; }
    });
    const listeners = new Map();
    const window = {
        location: { href: '' }, matchMedia: () => ({ matches: false }),
        addEventListener: (name, handler) => listeners.set(name, handler)
    };
    const context = vm.createContext({
        window, fetch,
        document: {
            documentElement: node(), head: node(),
            getElementById(id) { if (!nodes.has(id)) nodes.set(id, node()); return nodes.get(id); },
            createElement: node, addEventListener() {}, querySelectorAll: () => []
        },
        sessionStorage: {
            getItem: key => storage.get(key), setItem: (key, value) => storage.set(key, value),
            removeItem: key => storage.delete(key)
        },
        localStorage: { getItem: () => null, setItem() {} },
        performance: { getEntriesByType: () => [{ type: navigation }] },
        crypto: { randomUUID: () => 'new-tab-id' },
        setTimeout: () => 0,
        console: { error() {} }
    });
    vm.runInContext(script, context);
    return {
        window, storage, listeners,
        async load() { window.onload(); await vm.runInContext('updateQueue', context); },
        queue: () => vm.runInContext('updateQueue', context)
    };
}

test('updates are serialized and use the latest revision and CSRF header', async () => {
    const calls = [];
    let release;
    const app = browser(async (url, options) => {
        calls.push({ url, options });
        if (options.method !== 'POST') return response(200);
        if (calls.length === 2) return new Promise(resolve => { release = resolve; });
        return response(200, 'csrf-a', 3);
    });
    await app.load();
    const first = app.window.sendUpdate('name', 'Ada');
    const second = app.window.sendUpdate('save', true);
    await tick();
    assert.equal(calls.length, 2);
    release(response(200, 'csrf-a', 2));
    await Promise.all([first, second]);
    assert.equal(calls.length, 3);
    assert.equal(JSON.parse(calls[1].options.body).revision, 1);
    assert.equal(JSON.parse(calls[2].options.body).revision, 2);
    assert.equal(calls[2].options.headers['X-JUI-CSRF'], 'csrf-a');
    assert.equal(calls[2].options.credentials, 'same-origin');
    assert.equal(calls[2].url, '/ui?viewId=tab-a');
    assert.equal(app.window.juiSessionId, undefined);
});

for (const status of [401, 403, 409]) {
    test(`${status} refreshes the view and discards queued actions without replay`, async () => {
        const methods = [];
        let initial = true;
        const app = browser(async (url, options) => {
            methods.push(options.method || 'GET');
            if (initial) { initial = false; return response(200); }
            if (methods.length === 2) return response(status);
            return response(200, 'csrf-b', 1);
        });
        await app.load();
        await Promise.all([
            app.window.sendUpdate('old-save', true),
            app.window.sendUpdate('old-delete', true)
        ]);
        assert.deepEqual(methods, ['GET', 'POST', 'GET']);
        await app.window.sendUpdate('new-action', true);
        assert.deepEqual(methods, ['GET', 'POST', 'GET', 'POST']);
    });
}

test('successful logout changing the CSRF token also cancels queued old actions', async () => {
    const calls = [];
    const app = browser(async (url, options) => {
        calls.push(options.method || 'GET');
        return response(200, options.method === 'POST' ? 'csrf-after-logout' : 'csrf-a');
    });
    await app.load();
    await Promise.all([
        app.window.sendUpdate('logout', true),
        app.window.sendUpdate('private-save', true)
    ]);
    assert.deepEqual(calls, ['GET', 'POST']);
});

test('a newly navigated tab does not reuse its copied view id; reload retains it', () => {
    const duplicate = browser(async () => response(200), { navigation: 'navigate', storedView: 'copied-tab' });
    assert.equal(duplicate.window.juiViewId(), 'new-tab-id');
    assert.equal(duplicate.storage.get('jui-view-id'), 'new-tab-id');
    const reload = browser(async () => response(200), { storedView: 'existing-tab' });
    assert.equal(reload.window.juiViewId(), 'existing-tab');
});

test('Google navigation waits for pending updates and never exposes a session id', async () => {
    let release;
    const app = browser(async (url, options) => options.method === 'POST'
        ? new Promise(resolve => { release = resolve; }) : response(200));
    await app.load();
    app.window.sendUpdate('name', 'Ada');
    const login = app.window.juiGoogleLogin('/auth/google/login');
    await tick();
    assert.equal(app.window.location.href, '');
    release(response(200, 'csrf-a', 2));
    await login;
    assert.equal(app.window.location.href, '/auth/google/login');
});

test('focus refresh observes identity changes and cancels actions from the old view', async () => {
    const calls = [];
    const app = browser(async (url, options) => {
        calls.push(options.method || 'GET');
        return response(200, calls.length === 1 ? 'csrf-a' : 'csrf-b');
    });
    await app.load();
    app.listeners.get('focus')();
    await app.window.sendUpdate('old-action', true);
    assert.deepEqual(calls, ['GET', 'GET']);
});

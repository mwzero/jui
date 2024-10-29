let main = {exports:{}};
var script;

if (typeof fetch === 'undefined') {
  let dir = await import('path').then(path => path.dirname(import.meta.url).replace('file:/', ''));
  script = await import('fs').then(fs => fs.readFileSync(dir + '/classes.js', {encoding: 'utf8'}));
} else {
  script = await fetch('./classes.js').then(response => response.text());
}

Function('return function(module, exports) {\n' +
  script
  + '\nmain(); module.exports = main.exports;}')()(main, main.exports);

let hello = (...args) => main.exports.hello(args);
let greet = () => main.exports.greet();
let validate = (str) => main.exports.validate(str);

export {greet, hello, validate};
export default greet;

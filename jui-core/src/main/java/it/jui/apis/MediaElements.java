package it.jui.apis;

import it.jui.UIContext;

public class MediaElements extends BaseElements {

    public MediaElements(UIContext ctx) {
        super(ctx);
    }

    public void image(String src) {
        image(src, null);
    }

    public void image(String src, String caption) {
        String captionHtml = caption == null || caption.isBlank()
                ? ""
                : "<figcaption class='mt-2 text-sm text-gray-500 dark:text-gray-400'>" + escapeHtml(caption) + "</figcaption>";
        ctx.addHtml("<figure class='mb-5'><img src='" + escapeHtml(src)
                + "' alt='" + escapeHtml(caption == null ? "" : caption)
                + "' loading='lazy' class='max-w-full rounded-lg shadow-sm border border-gray-200 dark:border-gray-700'/>"
                + captionHtml + "</figure>");
    }

    public void audio(String src) {
        ctx.addHtml("<audio class='w-full mb-5' controls preload='metadata' src='" + escapeHtml(src) + "'></audio>");
    }

    public void video(String src) {
        video(src, true);
    }

    public void video(String src, boolean controls) {
        ctx.addHtml("<video class='w-full mb-5 rounded-lg bg-black' " + (controls ? "controls " : "")
                + "preload='metadata' src='" + escapeHtml(src) + "'></video>");
    }
}

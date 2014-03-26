package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class RenderCanvas extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderCanvas {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2092683606119630989L;

	protected native void createNativeInstance( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter renderTargetAdapter );
    protected native void releaseNativeInstance();

	protected synchronized native boolean acquireDrawingSurface();
	protected synchronized native void releaseDrawingSurface();
	protected synchronized native void swapBuffers();

    RenderCanvas( edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OnscreenRenderTarget onscreenRenderTarget ) {
        super( onscreenRenderTarget );
    }
}

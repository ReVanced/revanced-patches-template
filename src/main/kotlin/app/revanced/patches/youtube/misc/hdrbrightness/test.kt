package app.revanced.patches.youtube.misc.hdrbrightness

class test {

    /**
     * Class ghm
     *  @Override // defpackage.tgu
        public final void mX() {
            br D = this.a.h.D();
            if (D == null) {
                return;
            }
            WindowManager.LayoutParams attributes = D.getWindow().getAttributes();
            attributes.screenBrightness = -1.0f;
            D.getWindow().setAttributes(attributes);
        }

     */

    /**
     * revanced
     * .method public final mX()V
    iget-object v0, p0, Lghm;->a:Lghs;
    iget-object v0, v0, Lghs;->h:Lghg;
    invoke-virtual {v0}, Lghg;->D()Lbr;
    move-result-object v0
    if-nez v0, :cond_b
    return-void
    :cond_b
    invoke-virtual {v0}, Lbr;->getWindow()Landroid/view/Window;
    move-result-object v1
    invoke-virtual {v1}, Landroid/view/Window;->getAttributes()Landroid/view/WindowManager$LayoutParams;
    move-result-object v1
    const/high16 v2, -0x40800000    # -1.0f
    //ADD THIS:
    //invoke-static/range {v2 .. v2}, Lapp/revanced/integrations/patches/HDRMaxBrightnessPatch;->getHDRBrightness(F)F
    //move-result v2
    iput v2, v1, Landroid/view/WindowManager$LayoutParams;->screenBrightness:F
    invoke-virtual {v0}, Lbr;->getWindow()Landroid/view/Window;
    move-result-object v0
    invoke-virtual {v0, v1}, Landroid/view/Window;->setAttributes(Landroid/view/WindowManager$LayoutParams;)V
    return-void
     */
}
package app.revanced.patches.youtube.ad.suggestions.test;

public class kt {

    /**
     * @Override // defpackage.snu
     *     public final void j(boolean z) {
     *         this.n = app.revanced.integrations.patches.HideSuggestionsPatch.HideSuggestions(z);
     *         boolean z2 = this.k.b;
     *         h();
     *     }
     */


    /**
     * .method public final j(Z)V
     *     invoke-static/range {p1 .. p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
     *     move-result-object v0
     *     invoke-static {v0}, Lfi/razerman/youtube/XAdRemover;->RemoveSuggestions(Ljava/lang/Boolean;)Ljava/lang/Boolean;
     *     move-result p1
     *     iput-boolean p1, p0, Lsnq;->n:Z
     *     iget-object p1, p0, Lsnq;->k:Lsnp;
     *     iget-boolean p1, p1, Lsnp;->b:Z
     *     invoke-virtual {p0}, Lsnq;->h()V
     *     return-void
     * .end method
     */

}

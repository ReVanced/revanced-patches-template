package app.revanced.patches.youtube.layout.widesearchbar

class test {

    /**
     * get class jkg with parent-parent fp searching for strings:
     * "FEhistory"
     *  "FEmy_videos"
     *  "FEpurchases"
     */
    /**
     * //get this using parent fingerprint
     *  in jkg
     *
     * public final View i(akrl akrlVar, adpd adpdVar) {
    LayoutInflater from = LayoutInflater.from(this.a);
    this.g.h(false);
    ahnq ahnqVar = akrlVar.f;
    if (ahnqVar == null) {
    ahnqVar = ahnq.a;
    }
    HERE: if (fbn.aF(this.x)) {

     */

    /**
     * class fbn
     *
     * //old bH method from vanced
     *
     * public static boolean aF(txm txmVar) {
    akfd akfdVar = txmVar.b().e;
    if (akfdVar == null) {
    akfdVar = akfd.a;
    }
 //inject call to patch here   return akfdVar.V;
    }
     */


    //second method
    /*
    get class krf by searching for this method in krf using the string
    public static ies i(br brVar) {
        bp f = brVar.getSupportFragmentManager().f("VIDEO_QUALITIES_QUICK_MENU_BOTTOM_SHEET_FRAGMENT");
        if (f != null) {
            return (kga) f;
        }
        return new kga();
    }
:
    then get method
    public static jis h(Context context, txm txmVar, uag uagVar, txp txpVar) {
        return fbn.aB(txmVar) ? new jhx(context, txmVar, uagVar, txpVar) : jis.d;
    }
    in the same class by using opcodes, etc

    to then finally get the method in fbn.aB

    public static boolean aB(txm txmVar) {
        akfd akfdVar = txmVar.b().e;
        if (akfdVar == null) {
            akfdVar = akfd.a;
        }
 //inject call to patch here       return akfdVar.y;
    }
     */

}
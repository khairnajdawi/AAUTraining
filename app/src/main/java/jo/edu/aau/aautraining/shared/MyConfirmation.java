package jo.edu.aau.aautraining.shared;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import jo.edu.aau.aautraining.R;

public class MyConfirmation {
    private String msg = "";
    private Context ctx;
    private String posBtnText;
    private String negBtnText;
    private String title;

    public MyConfirmation(Context ctx, String msg) {
        this.msg = msg;
        this.ctx = ctx;
        this.posBtnText = ctx.getResources().getString(R.string.yes);
        this.negBtnText = ctx.getResources().getString(R.string.no);
        this.title = ctx.getResources().getString(R.string.please_confirm);
    }

    public MyConfirmation(Context ctx, String msg, String title) {
        this.msg = msg;
        this.ctx = ctx;
        posBtnText = ctx.getResources().getString(R.string.yes);
        negBtnText = ctx.getResources().getString(R.string.no);
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosBtnText(String posBtnText) {
        this.posBtnText = posBtnText;
    }

    public void setNegBtnText(String negBtnText) {
        this.negBtnText = negBtnText;
    }

    public void askConfirmation(DialogInterface.OnClickListener oclYes) {
        final AlertDialog.Builder myAlert = new AlertDialog.Builder(ctx);
        myAlert.setMessage(msg)
                .setTitle(title)
                .setNegativeButton(negBtnText, null)
                .setIcon(ctx.getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                .setPositiveButton(posBtnText, oclYes);
        AlertDialog alert = myAlert.create();
        alert.show();
    }

    public void askConfirmation(DialogInterface.OnClickListener oclYes, DialogInterface.OnClickListener oclNo) {
        final AlertDialog.Builder myAlert = new AlertDialog.Builder(ctx);
        myAlert.setMessage(msg)
                .setTitle(ctx.getResources().getString(R.string.please_confirm))
                .setNegativeButton(negBtnText, oclNo)
                .setIcon(ctx.getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                .setPositiveButton(posBtnText, oclYes);
        AlertDialog alert = myAlert.create();
        alert.show();
    }
}


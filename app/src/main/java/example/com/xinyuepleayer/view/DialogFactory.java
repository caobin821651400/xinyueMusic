package example.com.xinyuepleayer.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import example.com.xinyuepleayer.R;


public class DialogFactory {

	public static Dialog creatCommonDialog(Context context, String tip) {
		Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog_layout);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		TextView textView = (TextView) dialog.findViewById(R.id.tvLoad);
		textView.setText(tip);
		return dialog;
	}
	
	public static Dialog creatProgressDialog(Context context, String tip) {
		Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog_progress_download);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		TextView textView = (TextView) dialog.findViewById(R.id.tvLoad);
		textView.setText(tip);
		return dialog;
	}
}

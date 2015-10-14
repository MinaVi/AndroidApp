package com.sw.minavi.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class AsyncHttpRequest extends AsyncTask<Uri.Builder, Integer, Drawable> {

	private Context _context;
	private RelativeLayout _layout;
	private ImageView _imageView;
	private ProgressDialog progressDialog_;

	public AsyncHttpRequest(Context context, RelativeLayout layout, ImageView imageView) {
		this._context = context;
		this._layout = layout;
		this._imageView = imageView;
	}

	@Override
	protected void onPreExecute() {
//		progressDialog_ = new ProgressDialog(this._context);
//		progressDialog_.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressDialog_.setIndeterminate(false);
//		progressDialog_.show();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		progressDialog_.incrementProgressBy(progress[0]);
	}

	@Override
	protected void onPostExecute(Drawable result) {
//		progressDialog_.dismiss();
		//_imageView.setImageDrawable(result);
		_layout.setBackground(result);
	}

	@Override
	protected Drawable doInBackground(Uri.Builder... builder) {

		// onProgressUpdate(progress);作ってはみたものの、URLなので進捗がない(まあMinaviにはいらんやろ)
		return ImageOperations(builder[0].toString().substring(1));
	}

	private Drawable ImageOperations(String url) {
		try {
			return Drawable.createFromStream((InputStream) this.fetch(url), "src");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object fetch(String address) throws MalformedURLException, IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

}

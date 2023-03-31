package mod.tsd.activities;

import com.sketchware.remod.R;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;


public class DocumentationActivity extends AppCompatActivity {
	
	private HashMap<String, Object> MM = new HashMap<>();
	
	private ArrayList<HashMap<String, Object>> Data = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> Data2 = new ArrayList<>();
	
	private LinearLayout linear3;
	private ScrollView Main;
	private LinearLayout Log;
	private HorizontalScrollView hscroll1;
	private LinearLayout linear2;
	private ImageView imageview1;
	private TextView textview1;
	private TextView textview2;
	private LinearLayout linear4;
	private ImageView imageview2;
	
	private View LI;
	private Intent Link = new Intent();
	private RequestNetwork FetchFile;
	private RequestNetwork.RequestListener _FetchFile_request_listener;
	
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.documentation);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear3 = findViewById(R.id.linear3);
		Main = findViewById(R.id.Main);
		Log = findViewById(R.id.Log);
		hscroll1 = findViewById(R.id.hscroll1);
		linear2 = findViewById(R.id.linear2);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		textview2 = findViewById(R.id.textview2);
		linear4 = findViewById(R.id.linear4);
		imageview2 = findViewById(R.id.imageview2);
		FetchFile = new RequestNetwork(this);
		
		imageview2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				onBackPressed();
			}
		});
		
		_FetchFile_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				_listAllDataFromJSON(_response, linear2);
				Main.setVisibility(View.VISIBLE);
				Log.setVisibility(View.GONE);
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				textview1.setText("Failed to fetch data.");
				textview2.setText("Click here to go back.");
				textview2.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View _view) {
						onBackPressed();
						}
				});
			}
		};
	}
	
	private void initializeLogic() {
		Main.setVisibility(View.GONE);
		Log.setVisibility(View.VISIBLE);
		FetchFile.startRequestNetwork(RequestNetworkController.GET, "https://technicalstudiodeveloper.github.io/Sketchware-pro-data.github.io/main.json", "A", _FetchFile_request_listener);
	}
	
	public void _listAllDataFromJSON(final String _json, final View _view) {
		try{
			final JSONArray MyLoadedData = new JSONArray(_json);
			for(int _repeat95 = 0; _repeat95 < (int)(MyLoadedData.length()); _repeat95++) {
				JSONObject JSONObj = MyLoadedData.getJSONObject(_repeat95);

				if (JSONObj.has("title")) {
					LI = getLayoutInflater().inflate(R.layout.documentation_adapter, null);
					TextView title = ((TextView)LI.findViewById(R.id.textview1));
					final LinearLayout myl1 = ((LinearLayout)LI.findViewById(R.id.linear1));
					final LinearLayout myl2 = ((LinearLayout)LI.findViewById(R.id.linear2));
					final LinearLayout myl3 = ((LinearLayout)LI.findViewById(R.id.linear3));
					final ImageView img1 =((ImageView)LI.findViewById(R.id.imageview1));
					final ImageView img2 =((ImageView)LI.findViewById(R.id.imageview2));
					
					title.setText(JSONObj.getString("title"));
					myl3.setVisibility(View.GONE);
					img2.setVisibility(View.GONE);
					final int _r = _repeat95;
					
					if (JSONObj.has("clickable")) {
						if (JSONObj.getString("clickable").equals("true")) {
							final String url = JSONObj.getString("url");
							myl2.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View _view) {
									Link.setData(Uri.parse(url));
									Link.setAction(Intent.ACTION_VIEW);
									startActivity(Link);
									}
							});
						}
					}
					if (JSONObj.has("hasChild")) {
						if (JSONObj.getString("hasChild").equals("true")) {
							JSONArray JSArray = JSONObj.getJSONArray("Child");
							_listAllDataFromJSON(JSArray.toString(), myl3);
							if (myl3.getVisibility() == View.VISIBLE) {
								img1.setImageResource(R.drawable.ic_arrow_drop_up_black);
							}
							else {
								img1.setImageResource(R.drawable.ic_arrow_drop_down_black);
							}
							img1.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View _view) {
									if (myl3.getVisibility() == View.VISIBLE) {
										myl3.setVisibility(View.GONE);
										img1.setImageResource(R.drawable.ic_arrow_drop_down_black);
									}
									else {
										myl3.setVisibility(View.VISIBLE);
										img1.setImageResource(R.drawable.ic_arrow_drop_up_black);
									}
									}
							});
						}
						else {
							img1.setVisibility(View.INVISIBLE);
							img1.setEnabled(false);
						}
					}
					else {
						img1.setVisibility(View.INVISIBLE);
						img1.setEnabled(false);
					}
					if (JSONObj.has("ShouldShowFavicon")) {
						if (JSONObj.getBoolean("ShouldShowFavicon")) {
							if (JSONObj.has("url")) {
								img2.setVisibility(View.VISIBLE);
								Glide.with(getApplicationContext()).load(Uri.parse("https://t0.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=".concat(JSONObj.getString("url")).concat("&size=128"))).into(img2);
							}
						}
					}
					if (JSONObj.has("TextviewSize")) {
						title.setTextSize((int)JSONObj.getInt("TextviewSize"));
					}
					if (JSONObj.has("titleColor")) {
						try{
							_setTextColor(title, JSONObj.getString("titleColor"));
						}catch(Exception e){
							title.setText(title.getText().toString().concat(" ".concat("(Error : Invalid Color)")));
						}
					}
					if (myl1 != null) {
						((LinearLayout)myl1.getParent()).removeView(myl1);
					}
					((LinearLayout)_view).addView(myl1);
				}
			}
		}
		catch(JSONException e){
			 
		}
	}
	
	
	public void _setTextColor(final TextView _view, final String _color) {
		_view.setTextColor(Color.parseColor(_color));
	}
	
}

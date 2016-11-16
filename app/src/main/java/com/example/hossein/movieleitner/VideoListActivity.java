package com.example.hossein.movieleitner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoListActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSER = 1234;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private java.lang.String isFileChooserResultProccessed = "isProceed";
    private boolean fileProceedState = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Create the ACTION_GET_CONTENT Intent
        lunchFileChooser();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState == null){
            lunchFileChooser();
        }else {
            boolean proceed = savedInstanceState.getBoolean(isFileChooserResultProccessed,true);
            if(proceed){
                lunchFileChooser();
            }else {
                //doNothing
            }
        }
    }




    public void lunchFileChooser(){
        fileProceedState = false ;
        Intent getContentIntent = FileUtils.createGetContentIntent();

        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        startActivityForResult(intent, REQUEST_CHOOSER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {

                    final Uri uri = data.getData();

                    // Get the File path from the Uri
                    String path = FileUtils.getPath(this, uri);

                    // Alternatively, use FileUtils.getFile(Context, Uri)
                    if (path != null && FileUtils.isLocal(path)) {
                        File file = new File(path);

                        fileProceedState = true ;

                        Intent intent = new Intent(this, VideoPlayerActivity.class);
                        intent.putExtra("path" , path);
                        startActivity(intent);

                    }
                }
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(isFileChooserResultProccessed , fileProceedState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options , menu);

        MenuItem menuItem = menu.findItem(R.id.spinner);

        Spinner spinner = (Spinner) MenuItemCompat.getActionView(menuItem);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return true;
    }
}

package me.dreamheart.autoscalinglayout.sample;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();
        for( int i = 0; i < Shakespeare.TITLES.length; ++ i ){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", Shakespeare.TITLES[i]);
            map.put("ItemDialogue", Shakespeare.DIALOGUE[i]);
            listItems.add(map);
        }
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItems,
                R.layout.listview_item,
                new String[] {"ItemTitle", "ItemDialogue"},
                new int[ ] {R.id.title_text_view, R.id.dialogue_text_view}
        );
        setListAdapter(listItemAdapter);
    }
}

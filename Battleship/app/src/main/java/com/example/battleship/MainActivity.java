package com.example.battleship;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button[][] buttons = new Button[10][10];
    private TextView player;
    private Button buttonDrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = findViewById(R.id.text_view_player);
        for (int i =0; i < 10 ; i++){
            for (int j = 0; j < 10; j ++){
                    String buttonId = "button_"  + i + j;
                    int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
                    buttons[i][j] = findViewById(resId);
                myDragEventListener dragListen = new myDragEventListener();
                    buttons[i][j].setOnDragListener(dragListen);
            }
        }
        buttonDrag = findViewById(R.id.drag_button);
        buttonDrag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item("Success!");
                ClipData dragData = new ClipData(
                        "dragData",
                        new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                v.startDrag(dragData,myShadow,null,0);
                return true;
            }
            });
    }
    protected class myDragEventListener implements View.OnDragListener{
        public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                if(v.getTag() == "Green"){
                    return false;
                }
                switch(action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                            if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                                v.setBackgroundColor(Color.BLUE);
                                v.invalidate();
                                return true;
                            }
                            return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundColor(Color.GREEN);
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundColor(Color.BLUE);
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        CharSequence dragData = item.getText();
                        Toast.makeText(getApplicationContext(), "Dragged data is a " + dragData, Toast.LENGTH_LONG).show();
                        v.setBackgroundColor(Color.GREEN);
                        v.setTag("Green");
                        v.invalidate();
                        return true;
                     case DragEvent.ACTION_DRAG_ENDED:
                         v.setBackgroundColor(Color.GRAY);
                         v.invalidate();
                         if (event.getResult()) {
                             Toast.makeText(getApplicationContext(), "The drop was handled.", Toast.LENGTH_LONG).show();

                         }
                         else {
                                Toast.makeText(getApplicationContext(), "The drop didn't work.",Toast.LENGTH_LONG).show();

                         }
                         return true;
                         default:
                             Log.e("DragDrop Example", "Unknown action type received by OnDragListener");
                             break;
                }
                return false;
        }
    };
}

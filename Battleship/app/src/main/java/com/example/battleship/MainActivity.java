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
    private Button buttonDrag2;
    private Button buttonDrag3;
    private Button buttonRotate;

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
        buttonRotate = findViewById(R.id.rotate_button);
        buttonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonDrag.setRotation(buttonDrag.getRotation() + 90);
                buttonDrag2.setRotation(buttonDrag2.getRotation() + 90);
                buttonDrag3.setRotation(buttonDrag3.getRotation() + 90);
                int width = buttonDrag.getWidth();
                buttonDrag.setWidth(buttonDrag.getHeight());
                buttonDrag.setHeight(width);
                 width = buttonDrag2.getWidth();
                buttonDrag2.setWidth(buttonDrag2.getHeight());
                buttonDrag2.setHeight(width);
                 width = buttonDrag3.getWidth();
                buttonDrag3.setWidth(buttonDrag3.getHeight());
                buttonDrag3.setHeight(width);



            }
        });
        buttonDrag = findViewById(R.id.drag_button);
        buttonDrag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((String) v.getTag());
                ClipData dragData = new ClipData(
                        (String) v.getTag(),
                        new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                v.startDrag(dragData,myShadow,null,0);

                return true;
            }
            });
        buttonDrag2 = findViewById(R.id.drag_button2);
        buttonDrag2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((String) v.getTag());
                ClipData dragData = new ClipData(
                        (String) v.getTag(),
                        new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                v.startDrag(dragData,myShadow,null,0);
                return true;
            }
            });
        buttonDrag3= findViewById(R.id.drag_button3);
        buttonDrag3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((String) v.getTag());
                ClipData dragData = new ClipData((String) v.getTag(),
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
                        Toast.makeText(getApplicationContext(), item.getText(), Toast.LENGTH_LONG).show();
                        String dragData = (String)item.getText();
                        Toast.makeText(getApplicationContext(), "Dragged data is a " + dragData, Toast.LENGTH_LONG).show();
                        switch(dragData){
                            case "Red":
                                v.setBackgroundColor(Color.RED);
                                break;
                            case "Green":
                                v.setBackgroundColor(Color.GREEN);
                                break;
                            case "Orange":
                                v.setBackgroundColor(Color.YELLOW);
                                break;
                            default:
                                v.setBackgroundColor(Color.BLACK);
                        }
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

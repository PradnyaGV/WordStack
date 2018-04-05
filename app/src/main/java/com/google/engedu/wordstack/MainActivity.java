/* Word Stack
By Pradnya Valsangkar
 */

package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1;
    private String word2;
    private Stack<LetterTile> placedtiles=new Stack<>();
    Button undo;
    LinearLayout w1,w2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        undo= (Button) findViewById(R.id.button);
        w1= (LinearLayout) findViewById(R.id.word1);
        w2= (LinearLayout) findViewById(R.id.word2);
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                if(word.length()==WORD_LENGTH) {
                    words.add(word);
                }

            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        //word1LinearLayout.setOnTouchListener(new TouchListener());
       word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        //word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());

    }

   /* private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                System.out.println("placed tile :"+tile);
                tile.moveToViewGroup((ViewGroup) v);
                placedtiles.push(tile);
                System.out.println("placed tile :"+tile);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                return true;
            }
            return false;
        }
    }*/

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {

            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                        Toast.makeText(getApplicationContext(),"Game finished!!!",Toast.LENGTH_LONG).show();
                        //clearing linear layouts
                        if(((LinearLayout) w1).getChildCount() > 0)
                            ((LinearLayout) w1).removeAllViews();
                        if(((LinearLayout) w2).getChildCount() > 0)
                            ((LinearLayout) w2).removeAllViews();
                    }
                    LetterTile draged_tile = (LetterTile) event.getLocalState();
                    placedtiles.push(draged_tile);
                    return true;
            }
            return false;
        }
    }


    public boolean onStartGame(View view) {
        //clearing linear layouts
        if(((LinearLayout) w1).getChildCount() > 0)
            ((LinearLayout) w1).removeAllViews();
        if(((LinearLayout) w2).getChildCount() > 0)
            ((LinearLayout) w2).removeAllViews();

        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        int r1=random.nextInt(words.size());
        word1=words.get(r1);
        int r2=random.nextInt(words.size());
        word2=words.get(r2);

        System.out.println("Word 1::"+word1);
        System.out.println("Word 2::"+word2);
        //Words Scrambling
        String msg="";
        char word1arr[]=word1.toCharArray();
        char word2arr[]=word2.toCharArray();
        int counter1=0,counter2=0;
        while(counter1<word1arr.length || counter2<word2arr.length)
        {
            int randonCounter=random.nextInt(2)+1;
            if(randonCounter==1 && counter1<word1arr.length) {
                msg += word1arr[counter1];
                counter1++;
            }
            else if(randonCounter==2 && counter2<word2arr.length) {
                msg += word2arr[counter2];
                counter2++;
            }
        }
        StringBuffer revmsg=new StringBuffer(msg);
        revmsg.reverse();
        revmsg.toString();
        for(int i=0;i<revmsg.length();i++) {
            LetterTile letterTile=new LetterTile(getApplicationContext(),revmsg.charAt(i));
            stackedLayout.push(letterTile);
        }
        // messageBox.setText(msg);
        return true;
    }

    public boolean onUndo(View view) {
        LetterTile popped= (LetterTile) placedtiles.pop();
        popped.moveToViewGroup((ViewGroup) stackedLayout);
        return true;
    }
}

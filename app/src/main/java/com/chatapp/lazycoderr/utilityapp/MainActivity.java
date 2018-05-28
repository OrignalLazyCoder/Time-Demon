package com.chatapp.lazycoderr.utilityapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    public TextView Message;
    public ProgressBar TimeLeft;
    public Button Start;
    public Button GiveUp;
    public SeekBar Time;
    public TextView Dist;
    public EditText Value;
    public Button Score;
    public View windowView;
    public int time,D=0,giveup=0;
    private int[] colors = new int[]{
            Color.BLUE,Color.GRAY,Color.CYAN,Color.RED,Color.YELLOW,Color.WHITE,Color.GREEN,Color.MAGENTA
    };
    String Firstuse="0";
    int timefornot;
    String Givescore;
    public int t=time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Referring ID's
        windowView=findViewById(R.id.windowView);
        Message = (TextView) findViewById(R.id.ConfirmText);
        TimeLeft = (ProgressBar) findViewById(R.id.TimeBar);
        Time=(SeekBar) findViewById(R.id.seekBar);
        Start = (Button) findViewById(R.id.StartBTN);
        Score=(Button) findViewById(R.id.ScoreBTN);
        GiveUp = (Button) findViewById(R.id.GiveupBTN);
        Value = (EditText) findViewById(R.id.inputText);
        Dist =(TextView) findViewById(R.id.DistCountText);

        Time.setMax(60);
        Start.setEnabled(false);
        Dist.setText("Move SeekBar to enable Start Button.Pressing home button will act as giveup button.");
        GiveUp.setEnabled(false);

        readfile();
        if(Firstuse.equals("0")){
            Entry();
            try {
                savefile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Value.setEnabled(false);

        windowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time!=0){
                    stopPhub();
                    Vibrator v=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(3000);
                    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC,10000);
                    toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK,100);
                    Toast toast =Toast.makeText(getApplicationContext(),"Stop touching me!",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        Score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Firstuse.equals("0")){
                    Toast toast =Toast.makeText(getApplicationContext(),"First Use this Application and check your score next time.",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    Toast toast =Toast.makeText(getApplicationContext(),Givescore,Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });


        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                final String text;
                Time.setEnabled(true);
                if(Time.getProgress()==0) {
                        Time.setProgress(Integer.parseInt(Value.getText().toString()));
                }
               if(time==0){
                    Message.setText("Please Input time using seekbar above :)");
                    GiveUp.setEnabled(false);
                    D=0;
               }
               else{
                   final int t=time;
                   GiveUp.setEnabled(true);
                   Value.setEnabled(false);
                   final int len=colors.length;
                   Start.setEnabled(false);
                   Time.setEnabled(false);
                   text=time+" minutes left! ";
                   showNotification();
                    new CountDownTimer(time*1000*60,100){
                        public void onTick(long millisunitilfinished){
                            Random random =new Random();
                            int num=random.nextInt(len);
                            windowView.setBackgroundColor(colors[num]);
                            if(giveup==0)
                            {
                                timefornot= (int) (millisunitilfinished/1000);
                                Message.setText("Time Left "+millisunitilfinished/1000+" Seconds. Stay Focused.");
                                Dist.setText("Keep you Screen Upside down and do your work!");
                                updateTime();
                            }
                            else
                            {
                                onFinish();
                            }
                        }

                        public void onFinish() {
                            if (giveup==0)
                            {
                                Message.setText("Times Up!");
                                ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC,10000);
                                toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,1000000000);
                                Vibrator v=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(3000);
                                Start.setEnabled(true);
                                GiveUp.setEnabled(false);
                                Dist.setText("You can try Again.");
                                Time.setEnabled(true);
                                Time.setProgress(0);
                                windowView.setBackgroundColor(Color.WHITE);
                                FinishNoti();
                                try {
                                    SaveSuccess(t,getApplicationContext());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            else
                                Message.setText("You need to work on your Focus Power");
                        }
                    }.start();

                   }


            }
        });

        Time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                time=Time.getProgress();
                Message.setText("You are planning for "+time+" minutes?");
                Dist.setText("");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(Time.getProgress()==0){
                    Value.setEnabled(true);
                    time=10;
                }
                else{
                    Value.setEnabled(false);
                }
                Start.setEnabled(true);
                Dist.setText("");
            }
        });


         GiveUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 int left,t=time;
                 D=0;
                 giveup=1;
                 Time.setProgress(0);
                 GiveUp.setEnabled(false);
                 Start.setEnabled(true);
                 try {
                     SaveLoose(t,getApplicationContext());
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 onCreateDialogonlyexit();
             }
         });

        Score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    loadScore();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String Save=Givescore;
                Toast toast= Toast.makeText(getApplicationContext(),Save,Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    @Override
    public void onBackPressed(){

        onCreateDialog();
    }


    private void onCreateDialogonlyexit() {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("You have chosen to give up.Now try to train your mind and come back again!Accept That you have failed in your mission. \n\n Application By LazyCoderr .");
        alertDlg.setCancelable(false);

        alertDlg.setPositiveButton("Yes.. I Give up!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.super.onBackPressed();
            }
        });


        alertDlg.create().show();
    }

    private void onCreateDialog() {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("Are you sure you want to exit? \n\n\nApplication by LazyCoderr.");
        alertDlg.setCancelable(false);

        alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.super.onBackPressed();
            }
        });

        alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDlg.create().show();
    }

    private void Entry() {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("This Application will make you put your mobile phone screen upside down to focus on your work.There will be flashing colours on your screen which will surely cause Distraction. So put your mobile phone down for a specific period of time. \n\nTo input data Use textArea or Seekbar.");
        alertDlg.setCancelable(false);

        alertDlg.setPositiveButton("No.. I have more important work than this.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.super.onBackPressed();
            }
        });

        alertDlg.setNegativeButton("Yes.. I am in!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDlg.create().show();
    }



    private void readfile(){
        try{
            FileInputStream fin = openFileInput("openonce");
            InputStreamReader inputstream = new InputStreamReader(fin);
            BufferedReader bufferedReader = new BufferedReader(inputstream);
            StringBuilder stringBuilder = new StringBuilder();
            String line=null;
            while((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            fin.close();
            inputstream.close();
            Firstuse=stringBuilder.toString();
        } catch(java.io.IOException e){

        }
    }

    private void savefile() throws IOException {
        try{
            FileOutputStream fos=openFileOutput("openonce", Context.MODE_PRIVATE);
            Firstuse="1";
            fos.write(Integer.parseInt("1"));
            fos.close();
        }catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    private void showNotification(){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_phonelink).setContentTitle("You are all set!").setContentText("Timer set for "+time+" minutes");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,mBuilder.build());
        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC,10000);
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,1000000000);
    }

    private void updateTime(){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_filter).setContentTitle("You are good. Try to do your work").setContentText("Time left "+timefornot+" seconds");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,mBuilder.build());
    }

    private void FinishNoti(){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_assignment_turned_in_black_24dp).setContentTitle("Congratulations!").setContentText("You made it! Now you can take rest. Move seekbar to start again.");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,mBuilder.build());
    }

    private void stopPhub(){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_assignment_turned_in_black_24dp).setContentTitle("Aye!").setContentText("Do not mess with me or i Will format your Mobile!");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4,mBuilder.build());

    }

    private void SaveSuccess(int ttime,Context context) throws IOException {
        OutputStreamWriter outputStreamWriter= new OutputStreamWriter(context.openFileOutput("SaveFile.txt",Context.MODE_PRIVATE));
        outputStreamWriter.write("You Focused "+ttime+" minutes");
        outputStreamWriter.close();
        popuptoast();
    }

    private void SaveLoose(int ttime,Context context) throws IOException {
        OutputStreamWriter outputStreamWriter= new OutputStreamWriter(context.openFileOutput("SaveFile.txt",Context.MODE_PRIVATE));
        outputStreamWriter.write("You Failed to focus "+ttime+" minutes");
        outputStreamWriter.close();
        popuptoast();
    }

    private void loadScore() throws IOException {
        try{
            FileInputStream fin = openFileInput("SaveFile.txt");
            InputStreamReader inputstream = new InputStreamReader(fin);
            BufferedReader bufferedReader = new BufferedReader(inputstream);
            StringBuilder stringBuilder = new StringBuilder();
            String line=null;
            while((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            fin.close();
            inputstream.close();
            Givescore=stringBuilder.toString();
        } catch(java.io.IOException e){

        }

    }

    public void popuptoast(){
        Toast toast=Toast.makeText(getApplicationContext(),"Score Saved!",Toast.LENGTH_SHORT);
        toast.show();
    }
}

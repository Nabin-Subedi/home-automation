package com.example.bishal.smartoutlet;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import at.markushi.ui.CircleButton;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


public class MainActivity extends AppCompatActivity {
   // JSONObject a;

//Button a;
    CircleButton b;
    RequestQueue queue;
    String res="";
    private DatabaseReference mPostReference;
    TextView t1;
    TextView txt1;
    LineChartView chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getApplicationContext());
        mPostReference = FirebaseDatabase.getInstance().getReference().child("arduinolog");
        mPostReference.limitToLast(10);

        setContentView(R.layout.activity_main);
        t1=(TextView)findViewById(R.id.text1);
        txt1=(TextView)findViewById(R.id.txt1);
String res=httprequest("https://smartpower-6d865.firebaseio.com/state.json");


      //  t1.setText("---");
   //a=(Button)findViewById(R.id.btn1);
        b=(CircleButton) findViewById(R.id.btn2);
        chart = (LineChartView)findViewById(R.id.chart);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer resource = (Integer)b.getTag();
                Log.e("tag id",resource.toString());
               // t1.setText("");
                if(resource==2130837591)
                {
statechange(1);

                }
               else if(resource==2130837592)
                {
statechange(0);
                }
              //  httprequest("http://data.sparkfun.com/input/pwK3yl2Y7EIXwoANd2XM?private_key=64gEm2p6nMIrk6JVo0rm&node1=0&node2=1");
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
              Intent r=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(r);

            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String clubkey = dataSnapshot.getKey();
                // Get Post object and use the values to update the UI
                ArrayList<Float> listtemp = new ArrayList<Float>();
                ArrayList<String> listtimestamp = new ArrayList<String>();
                String status = (String) dataSnapshot.child("status").getValue();
                float temp;
                txt1.setText("Online");
                txt1.setTextColor(getResources().getColor(R.color.green));
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {


                    String timestamp =getDateCurrentTimeZone((Long) messageSnapshot.child("timestamp").getValue());

                         temp = Float.valueOf((String)messageSnapshot.child("temperature").getValue());


                    listtemp.add(temp);
                    listtimestamp.add(timestamp);
                }
                Post post = dataSnapshot.getValue(Post.class);
                String a=(String)dataSnapshot.child("status").getValue();
                String b=post.status;
                makechart(listtemp);

                // [START_EXCLUDE]
               // mAuthorView.setText(post.author);
               // mTitleView.setText(post.title);
                //mBodyView.setText(post.body);
                // [END_EXCLUDE]
            }
            public  String getDateCurrentTimeZone(long timestamp) {
                try{
                    Calendar calendar = Calendar.getInstance();
                    TimeZone tz = TimeZone.getDefault();
                    calendar.setTimeInMillis(timestamp * 1000);
                    calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currenTimeZone = (Date) calendar.getTime();

                    return sdf.format(currenTimeZone);
                }catch (Exception e) {
                }
                return "";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("tag", "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(MainActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
     //   mPostListener = postListener;

        // Listen for comments
      //  mAdapter = new CommentAdapter(this, mCommentsReference);
       // mCommentsRecycler.setAdapter(mAdapter);


    }

    void makechart(ArrayList<Float> a)
    {

        chart.setViewportCalculationEnabled(false);


        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 10;
        v.top = 40;
        v.left = 0;
        v.right = 15;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);

        List<PointValue> values = new ArrayList<PointValue>();
        int i;
        for(i=0;i<a.size();i++) {

            values.add(new PointValue(i,a.get(i)));

        }
        i=i-1;
        t1.setText("Temperature "+a.get(i));

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);


        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis().generateAxisFromRange(0,15,2);
        Axis axisY = new Axis().generateAxisFromRange(10,40,2).setAutoGenerated(false).setHasLines(true);



        axisX.setName("Time");
        axisY.setName("Degree Celcius");

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);


        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        //ColumnChartData.setStacked(boolean isStacked);
        line.setStrokeWidth(3);



        //LineChartView chart = new LineChartView(context);
        chart.setLineChartData(data);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    String httprequest(String url) {


        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      //  t1.setText(response);
                        res=response;
                        if(res.equals("0")||res.equals("1"))
                        setButtonState(res);
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mTextView.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.

        queue.add(stringRequest2);

return res;
    }


    void setButtonState(String abc)


    {

      // t1.setText(abc);
        if(abc.equals("0"))
        {
b.setImageResource(R.drawable.light_off);
            b.setTag(R.drawable.light_off);
        }
else if(abc.equals("1"))
        {
b.setImageResource(R.drawable.light_on);

            b.setTag(R.drawable.light_on);}

    }



void statechange(int state)
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("state");

    myRef.setValue(state);
    if(state==0)
    {
        b.setImageResource(R.drawable.light_off);
        b.setTag(R.drawable.light_off);
    }
else if(state==1)
    {
        b.setImageResource(R.drawable.light_on);
        b.setTag(R.drawable.light_on);
    }
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

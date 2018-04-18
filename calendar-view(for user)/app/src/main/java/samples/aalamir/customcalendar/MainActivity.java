package samples.aalamir.customcalendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import samples.aalamir.customcalendar.OfflineData.FeedReaderDbHelper;
import samples.aalamir.customcalendar.OfflineData.MyDatabases;
import samples.aalamir.customcalendar.OnlineData.DataModel;
import samples.aalamir.customcalendar.OnlineData.OnlineDatabases;



public class MainActivity extends ActionBarActivity
{
	static MainActivity instance;
	static HashSet<Date> events;
	public static FeedReaderDbHelper mDbHelper;
    public static CalendarView cv;
    TextView displayEvents;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		instance = this;

		mDbHelper = new FeedReaderDbHelper(this);

		events = new HashSet<>();

		displayEvents = (TextView)findViewById(R.id.display_events);

		fetchFromDatabase();


		cv = ((CalendarView)findViewById(R.id.calendar_view));
		cv.updateCalendar(events);

		// assign event handler
		cv.setEventHandler(new CalendarView.EventHandler()
		{
			@Override
			public void onDayLongPress(Date date)
			{
				// show returned day
				DateFormat df = SimpleDateFormat.getDateInstance();
				Toast.makeText(MainActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDayPress(Date date){
				// show returned day
				SimpleDateFormat curFormat = new SimpleDateFormat("dd/MM/yyyy");
				//displayEvents.setText(curFormat.format(date));
				String DD = curFormat.format(date);
				String dayo = DD.substring(0,2);
				String montho = DD.substring(3,5);
				String yearo = DD.substring(8,10);

				SQLiteDatabase db = mDbHelper.getReadableDatabase();
				Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

				if(c.getCount()==1){
					displayEvents.setText("No Calendar.");
				}

				if (c.moveToFirst()) {
					while ( !c.isAfterLast() ) {
						//Toast.makeText(this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
						if(!"android_metadata".equals(c.getString(0))) {
							// MyCalendarsList.add(new DataModel(c.getString(0)));
							Cursor cursor = db.rawQuery("SELECT * FROM "+c.getString(0),null);
							int flag = 0;
							if(cursor.moveToFirst()){
								do{


									String dat = cursor.getString(1);
									String day = dat.substring(0,2);
									String month = dat.substring(3,5);
									String year = dat.substring(6,8);

									if(dayo.equals(day) && montho.equals(month) && yearo.equals(year)){
										if(flag==1)
											displayEvents.setText(displayEvents.getText()+"==> "+cursor.getString(2)+"\n");
										if(flag==0)
											displayEvents.setText("==> "+cursor.getString(2)+"\n");

										if(cursor.moveToNext()){
											String datw = cursor.getString(1);
											String dayw = datw.substring(0,2);
											String monthw = datw.substring(3,5);
											String yearw = datw.substring(6,8);

											if(!dayw.equals(day) || !monthw.equals(month) || !yearw.equals(year)){
												break;
											}

											flag = 1;
										}
										cursor.moveToPrevious();
									}else {
										displayEvents.setText("No Event.");
									}


								}while (cursor.moveToNext());
							}
							cursor.close();
						}
						c.moveToNext();
					}
				}

				c.close();
			}
		});
	}

	public static MainActivity getInstance(){return instance;}

	public static void updateCalendarAgain(){
	    events.clear();
        fetchFromDatabase();
        cv.updateCalendar(events);
    }



	public static HashSet<Date> getEvents(){return events;}

	public static void fetchFromDatabase(){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                //Toast.makeText(this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                if(!"android_metadata".equals(c.getString(0))) {
                   // MyCalendarsList.add(new DataModel(c.getString(0)));
                    Cursor cursor = db.rawQuery("SELECT * FROM "+c.getString(0),null);
                    if(cursor.moveToFirst()){
                        do{
                            String date = cursor.getString(1);
                            String day = date.substring(0,2);
                            String month = date.substring(3,5);
                            String year = date.substring(6,8);

                            int dayi = Integer.parseInt(day);
                            int monthi = Integer.parseInt(month);
                            int yeari = Integer.parseInt(year);
                            yeari = yeari + 2000;

                            events.add(new Date(yeari-1900,monthi-1,dayi-0));
                        }while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                c.moveToNext();
            }
        }

        c.close();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}
		if (id== R.id.action_cloudDatabases){
			Intent intent = new Intent(this,OnlineDatabases.class);
			startActivity(intent);
		}
		if(id == R.id.action_myDatabases){
			Intent intent = new Intent(this, MyDatabases.class);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}
}

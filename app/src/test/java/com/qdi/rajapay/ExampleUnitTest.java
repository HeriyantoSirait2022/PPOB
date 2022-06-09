package com.qdi.rajapay;

import android.os.Handler;

import com.qdi.rajapay.home.AccountFragment;
import com.qdi.rajapay.main_menu.water.WaterInputNoActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void getCH(){
        System.out.println(WaterInputNoActivity.isPdamCh1("PDAM400011"));
    }

    @Test
    public void testPhoneNo(){
        System.out.println(AccountFragment.formatPhoneNumber("6282345678"));
    }

    @Test
    public void testThread(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("bbb");
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second

//        final Handler handler = new Handler();
//        final int delay = 1000; // 1000 milliseconds == 1 second
//
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                System.out.println("myHandler: here!"); // Do your work here
//                handler.postDelayed(this, delay);
//            }
//        }, delay);

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try {  // Let no Exception reach the ScheduledExecutorService.
                    System.out.println("aaa");
                } catch ( Exception e ) {
                    System.out.println( "ERROR - unexpected exception1" );
                }
            }
        }, 0, 2000);

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        /*This schedules a runnable task every second*/
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {  // Let no Exception reach the ScheduledExecutorService.
                    System.out.println("bbb");
                } catch ( Exception e ) {
                    System.out.println( "ERROR - unexpected exception" );
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
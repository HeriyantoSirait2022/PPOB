package com.qdi.rajapay.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.VolleyError;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.contact_us.ContactUsConfirmationActivity;
import com.qdi.rajapay.inbox.InboxListActivity;
import com.qdi.rajapay.inbox.news.DetailNotificationActivity;
import com.qdi.rajapay.inbox.ticket.DetailTicketActivity;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.enums.NotificationType;
import com.qdi.rajapay.model.enums.TransactionType;
import com.qdi.rajapay.order.OrderDetailActivity;

public class NotificationManager implements APICallback.ItemCallback<OrderData>{
    private Context mCtx;
    private static NotificationManager mInstance;
    private String title, body, idOrder, txnType;

    private NotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized NotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotificationManager(context);
        }
        return mInstance;
    }

    public void intentToTransactionDetail(SharedPreferences user_sp, String title, String body, String idOrder, String txnType){
        TransactionAPI api;
        TransactionType transactionType;
        OrderData selected;

        this.title = title;
        this.body = body;
        this.idOrder = idOrder;
        this.txnType = txnType;

        api = new TransactionAPI(mCtx, user_sp);
        selected = new OrderData();
        transactionType = TransactionType.fromString(this.txnType);

        selected.setIdOrder(this.idOrder);
        selected.setTypeTxn(transactionType);
        api.getOrderDetail(selected, NotificationManager.this);
    }

    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes 0911254300-210 W05 Ticket notification update
     *
     * @param title
     * @param body
     * @param idTicket
     */
    // <code>
    public void intentToTicketDetail(String title, String body, String idTicket){
        Intent resultIntent = new Intent(mCtx, DetailTicketActivity.class);
        resultIntent.putExtra("caller", this.getClass().getCanonicalName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("idTicket", idTicket);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mCtx);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        displayNotification(title, body, pendingIntent);
    }
    // </code>

    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes 0911254300-210 W03 News notification update
     *
     * @param title
     * @param body
     * @param idNews
     */
    // <code>
    public void intentToNewsDetail(String title, String body, String idNews){
        Intent resultIntent = new Intent(mCtx, DetailNotificationActivity.class);
        resultIntent.putExtra("caller", this.getClass().getCanonicalName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("idNews", idNews);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mCtx);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        displayNotification(title, body, pendingIntent);
    }
    // </code>


    public void displayNotification(String title, String body, PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.CHANNEL_ID_01)
                        .setContentTitle(title)
                        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setAutoCancel(true)
                        .setContentText(body);

        mBuilder.setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_notification);
            mBuilder.setColor(mCtx.getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_notification);
        }

        NotificationManagerCompat mNotifyMgr =
                NotificationManagerCompat.from(mCtx);

        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, mBuilder.build());
        }
    }

    @Override
    public void onAPIResponseFailure(VolleyError error) {
        String strError = error.getLocalizedMessage();
        String strMsg = error.getMessage();
    }

    @Override
    public void onItemResponseSuccess(OrderData item, String message) {
        Intent resultIntent = new Intent(mCtx, OrderDetailActivity.class);
        resultIntent.putExtra("caller", this.getClass().getCanonicalName())
                .putExtra("data",item);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mCtx);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        displayNotification(title, body, pendingIntent);
    }
}

package com.c_m_p.roll_three_dice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class MyBroadcastReceiver extends BroadcastReceiver {

    IEventNetwork iEventNetwork;
    public MyBroadcastReceiver(IEventNetwork iEventNetwork){
        this.iEventNetwork = iEventNetwork;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Nếu "intent.getAction()" == Connectivity.CONNECTIVITY_ACTION thì
        // chúng ta đã lắng nghe được sự thay đổi của Network
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            if(isNetworkAvailable(context)){
//                Toast.makeText(context, "thong20 - Internet connected", Toast.LENGTH_LONG).show();
                iEventNetwork.onResultNetworkAvailable(true);
            }else {
//                Toast.makeText(context, "thong20 - Internet disconnected", Toast.LENGTH_LONG).show();
                iEventNetwork.onResultNetworkAvailable(false);
            }
        }

    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null){
            return false;
        }

        // Từ Android 6 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Khi gọi hàm .getActiveNetwork(), nếu bị báo đỏ, ta phải thêm
            // permission "ACCESS_NETWORK_STATE" vào "AndroidManifest.xml"
            // hoặc nhấn Alt+Enter để xem gợi ý
            Network network = connectivityManager.getActiveNetwork();
            if(network == null){
                return false;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);

        }else{
            // Khi gọi hàm .getActiveNetworkInfo() ta phải thêm
            // permission "ACCESS_NETWORK_STATE" vào "AndroidManifest.xml"
            // hoặc nhấn Alt+Enter để xem gợi ý
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

    }

}

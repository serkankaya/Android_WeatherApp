package com.example.host.havadurumu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText ara;
    ListView list;
    List<String> ls = new ArrayList<>();
    TextView bilgi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ara=(EditText)findViewById(R.id.aratxt);
        list=(ListView)findViewById(R.id.listView);
        bilgi=(TextView)findViewById(R.id.bilgitxt);
        ara.setText("");
        String durl="https://query.yahooapis.com/v1/public/yql?q=select%20item%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places%20where%20text%3D%27istanbul%27)%20and%20u%3D%27c%27&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        new havaDurumu(durl,this).execute();

    }

    public void fncAra(View v){
        ls.clear();
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20item%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places%20where%20text%3D%27"+ara.getText().toString().trim()+"%27)%20and%20u%3D%27c%27&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        ara.setText("");
        new havaDurumu(url,this).execute();
    }
    class havaDurumu extends AsyncTask<Void,Void,Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";
        public havaDurumu(String url, Activity ac) {
            this.url = url;
            pro = new ProgressDialog(ac);
            pro.setMessage("Lütfen Bekleyiniz !");
            pro.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                data = Jsoup.connect(url).ignoreContentType(true).execute().body();
            }catch (Exception ex) {
                Log.d("Json Hatası ", ex.toString());
            }finally {
                pro.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            try {
                ls.clear();
                JSONObject obj = new JSONObject(data);
                JSONObject query=obj.getJSONObject("query");
                JSONObject results=query.getJSONObject("results");
                JSONArray channel=results.getJSONArray("channel");
                JSONObject item=channel.getJSONObject(0).getJSONObject("item");
                JSONObject condition=item.getJSONObject("condition");
                String tarih=condition.getString("date");
                String bsicaklik=condition.getString("temp");
                String genelDurum=condition.getString("text");
                String duzenlenenHavaDurumu="Bugünün Hava Durumu Tahmini"+"\n"+"Sıcaklık : "+bsicaklik+"°\n"+"Durum : "+genelDurum;
                bilgi.setText(duzenlenenHavaDurumu);
                JSONArray forecast=item.getJSONArray("forecast");
                for (int j=0;j<forecast.length();j++){
                    String atarih=forecast.getJSONObject(j).getString("date");
                    String gun=forecast.getJSONObject(j).getString("day");
                    String enYuksek=forecast.getJSONObject(j).getString("high");
                    String enDusuk=forecast.getJSONObject(j).getString("low");
                    String aGenelDurum=forecast.getJSONObject(j).getString("text");
                    if(gun.equals("Sun")){
                        gun="Pazar";
                    }else if (gun.equals("Mon")){
                        gun="Pazartesi";
                    }else if(gun.equals("Tue")){
                        gun="Salı";
                    }else if(gun.equals("Wed")){
                        gun="Çarşamba";
                    }else if(gun.equals("Thu")){
                        gun="Perşembe";
                    }else if(gun.equals("Fri")){
                        gun="Cuma";
                    }else if(gun.equals("Sat")){
                        gun="Cumartesi";
                    }
                    String gDuzenlenenHavaDurumu="Tarih : "+atarih+" "+gun+"\nEn Yüksek Sıcaklık : "+enYuksek+"\nEn Düşük Sıcaklık : "+enDusuk+"\nGenel Durum : "+aGenelDurum;
                    ls.add(gDuzenlenenHavaDurumu);
                }

                ArrayAdapter<String> adp=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,ls);
                list.setAdapter(adp);
                           } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

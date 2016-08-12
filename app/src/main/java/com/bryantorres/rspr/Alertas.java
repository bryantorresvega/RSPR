package com.proj.act;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils; //libreria que importe para capitalizar las primeras letras de las regiones
import android.content.Intent;

public class alertas extends functions{

    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); //are used to send values from android app to server.
    InputStream is = null;
    StringBuilder sb=null;
    String result = null;
    JSONArray jArray;
    TextView txView, fechatext;
    char acento_i='\u00ED';
    char acento_o='\u00f3';

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertas);


        boolean check = false;
        chkStatus(check);
        if(chkStatus(check)==true){
            try {
                GetNotification();    //LLama GetNotification si no hay errores de conneción
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "Conecci"+acento_i+"n de red no establacida.", Toast.LENGTH_SHORT).show();
            //finish();
        }

    }

    public void GetNotification() throws IOException{

        URL oracle = null;
        try {
            oracle = new URL("http://www.prsn.uprm.edu/Data/prsn/EarlyWarning/Catalogue.txt");  //LLama el URL del catalogo oracle para despues abrirle con inputstream reader
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String alertString = "";       //un espacio de string
        String inputLine;

        inputLine = in.readLine();     //lee la linea del catalogo
        alertString += inputLine;      //añade la linea de del catalogo

        in.close();                    //cierra el instream

        Scanner filter   = new Scanner(alertString);

        SeismicData temp = new SeismicData();

        temp.setSeismicId  (filter.next());
        temp.setMagnitude  (filter.next());
        temp.setMagnitudType(filter.next());
        temp.setSource    (filter.next());
        temp.setDate      (filter.next());
        temp.setTime      (filter.next());
        temp.setLatitud       (filter.next());
        temp.setLongitud   (filter.next());
        temp.setDepth     (filter.next());
        if(filter.hasNext("0"))
        {
            filter.next();
            temp.setOutside_region(filter.next());
        }
        else{
            temp.setPR_region(filter.next());
            filter.next();
        }
        temp.setSeismicIntencity(filter.next());


        URL webAlert = null;
        try {
            webAlert = new URL("http://prsncluster.uprm.edu/mobileapp/Data/prsn/EarlyWarning/WEBalert.txt");  //LLama el URL del webAlert oracle para despues abrirle con inputstream reader
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader on = null;
        try {
            on = new BufferedReader(
                    new InputStreamReader(webAlert.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //un espacio de string
        String line;
        String aviso;

        line = on.readLine();     //lee la linea del catalogo
        aviso = line.substring(line.lastIndexOf(" ")+1);     //añade la linea de del catalogo

        on.close();                    //cierra el instream


        TextView alertaText = (TextView) findViewById(R.id.AlertatextView);
        TextView fecha      = (TextView) findViewById(R.id.fechatextView);
        TextView magnitud   = (TextView) findViewById(R.id.magnitudView);
        TextView localizacion = (TextView) findViewById(R.id.localizacion);//cree el texto en el layout

        HashMap<String, String> diferentAlerts= new HashMap<String, String>();

        diferentAlerts.put("1", "No hay Aviso, Advertencia o Vigilancia de tsunami para Puerto Rico e Islas V"+acento_i+"rgenes.");
        diferentAlerts.put("2", "Una Vigilancia de tsunami esta en efecto para Puerto Rico e Islas V"+acento_i+"rgenes.");
        diferentAlerts.put("3", "Una Advertencia de tsunami esta en efecto para Puerto Rico e Islas V"+acento_i+"rgenes.");
        diferentAlerts.put("4", "Un Aviso de tsunami esta en efecto para Puerto Rico e Islas V"+acento_i+"rgenes.");
        diferentAlerts.put("5", "Cancelación de Aviso, Advertencia o Vigilancia de tsunami para Puerto Rico e Islas V"+acento_i+"rgenes.");



        alertaText.setText(diferentAlerts.get(aviso));//
        magnitud.setText("Magnitud: "+ temp.getMagnitude());
        if(aviso.equals("1")) {
            alertaText.setBackgroundColor(Color.parseColor("#197319"));
        }
        if(aviso.equals("2")) {
            alertaText.setBackgroundColor(Color.YELLOW);
        }
        if(aviso.equals("3")) {
            alertaText.setBackgroundColor(Color.parseColor("#f18b40"));
        }
        if(aviso.equals("4")) {
            alertaText.setBackgroundColor(Color.RED);
        }
        if(aviso.equals("5")) {
            alertaText.setBackgroundColor(Color.GREEN);
        }
        localizacion.setText("Localizaci"+acento_o+"n: "+ WordUtils.capitalizeFully(temp.getPR_region().toLowerCase())); //la funcion get esta en SeismicData busca - esta funcion busca la magnitud y la region del terremoto
        fecha.setText(temp.getDate() + " " + temp.getTime());

    }

    public void onBackPressed(){
        Intent intent = new Intent(this, MapTest.class);
        startActivity(intent);
        finish();
    }
}
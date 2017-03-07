package com.kitkat.android.httpurlconnection_openapi;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/** HttpURLConnection_OpenAPI
 *  (서울시 공공 데이터 Open API 다루기)
 *
 *  사전준비
 *      1. 서울 열린 데이터광장 접속 및 회원가입 (http://data.seoul.go.kr/)
 *      2. OpenAPI 사용을 위한 인증키 발급
 *      3. OpenAPI 검색
 *
 *      4. 사용할 OpenAPI URL 주소에 인증키 포함
 *         (Ex. http://openapi.seoul.go.kr:8088/(인증키)/xml/SdeGiGhospP2015WGS/1/20/)
 *
 *      5. OpenAPI 가 제공할 Data Type 지정을 위해 URL 주소에 Data Type 규정
 *         (JSON -> http://swopenapi.seoul.go.kr/api/subway/sample/xml/realtimeStationArrival/0/5/)
 *         (XML  -> http://swopenapi.seoul.go.kr/api/subway/sample/json/realtimeStationArrival/0/5/)
 *
 *  서로 다른 언어들 간 데이터를 주고 받는 방식 (XML, JSON)
 *      각각의 언어들은 각각의 형식과 다른 형식의 데이터를 사용 그러므로, 언어들 간에 데이터 타입이 호환되지 않는다.
 *      (JavaScript 의 배열과 Java 의 배열은 컨셉은 같으나, 다른 체계와 데이터를 사용)
 *
 *  XML (eXtensible Markup Language)
 *      XML 은 HTML 과 마찬가지로 TAG 를 통해 데이터를 표현하므로 XML 자체는 언어와 상관없는 중립적인 형태의 텍스트 표현식
 *
 *      - 각 언어는 XML 파서를 통해 XML 텍스트를 해석하여 데이터 추출
 *      - TAG 반복으로 빠른속도로 데이터가 증가하므로 무겁다.
 *      - XML 자체는 텍스트로 이루어진 문서이므로, 각 언어는 복잡한 과정을 통해 해석 및 변환
 *
 *      <?xml version="1.0" encoding="utf-8"?>
 *      <manifest xmlns:android="http://schemas.android.com/apk/res/android"
 *          package="com.kitkat.android.httpurlconnection_openapi">
 *
 *          <application
 *              android:allowBackup="true"
 *              android:icon="@mipmap/ic_launcher"
 *              android:label="@string/app_name"
 *              android:supportsRtl="true"
 *              android:theme="@style/AppTheme">
 *
 *              <meta-data
 *                  android:name="com.google.android.geo.API_KEY"
 *                  android:value="@string/google_maps_key" />
 *
 *              <activity
 *                  android:name=".MapsActivity"
 *                  android:label="@string/title_activity_maps">
 *                  <intent-filter>
 *                      <action android:name="android.intent.action.MAIN" />
 *                      <category android:name="android.intent.category.LAUNCHER" />
 *                  </intent-filter>
 *              </activity>
 *          </application>
 *      </manifest>
 *
 *  JSON (JavaScript Object Notation)
 *      JavaScript 에서 객체를 만들 때 사용하는 텍스트 표현식
 *
 *      - JavaScript 의 객체, 배열을 그대로 다른 언어에 전송할 수 있는 데이터 표준
 *      - JavaScript 의 객체, 배열은 다른 언어에 그대로 전송 불가
 *      - 각 언어들이 JavaScript 문법에 기초한 JSON 표현식을 해석해서 데이터 변환
 *      - XML 보다 경량화 된 데이터 표준 교환 방식
 *
 *      JSON Object
 *          var person = { "Key" : Value, "Key" : Value }
 *          JSON 표현식의 Text 를 만들어서 실행하면 JavaScript 해석기는 2개의 프로퍼티를 가진 객체 생성
 *
 *          - JavaScript 객체는 { }로 감싸는 규칙
 *          - 값과 값 사이는 콤마로 ( , ) 구분
 *          - 값의 이름과(Key) 값은(Value) 세미콜론으로 ( ; ) 구분
 *
 *      JSON Array
 *          var member = [ "Bomi", "Chorong", "Namju" ]
 *
 *          - [ ]로 감싸고, 값과 값 사이는 콤마로 ( , ) 구분
 *          - 값이 JSON Object 가 될 수 있다.
 *
 *      { // All JSON String -> JSONObject jsonObject = new JSONObject(jsonString);
 *
 *          "SearchParkingInfoRealtime": { // Root JSON Object ->  JSONObject rootObject = jsonObject.getJSONObject("SearchParkingInfoRealtime");
 *              "list_total_count": 460,
 *
 *              "RESULT": {
 *                  "CODE": "INFO-000",
 *                  "MESSAGE": "정상 처리되었습니다"
 *              },
 *
 *              "row": [ // JSONArray ->  JSONArray rows = rootObject.getJSONArray("row");
 *
 *                  { // JSONObject -> JSONObject parkObject = rows.getJSONObject(i);
 *                      "PARKING_CODE": "1037932",
 *                      "PARKING_NAME": "구로디지털단지역 환승주차장(시)",
 *                      "LAT": "37.48543179",
 *                      "LNG": "126.90124331", // Key - Value -> double lat = parkObject.getDouble("LAT");
 *                  },
 *
 *                  {
 *                      "PARKING_CODE": "172198",
 *                      "PARKING_NAME": "구의1동 공영주차장(구)",
 *                      "LAT": "37.53828214",
 *                      "LNG": "127.08789174",
 *                  }
 *              ]
 *          }
 *      }
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Http.Callback {

    private GoogleMap mMap;
    // OpenAPI URL
    private String urlString = "http://openapi.seoul.go.kr:8088/666569554d63686f36356b6f5a615a/json/SearchParkingInfoRealtime/1/1000/";
    private String koreanParameter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            koreanParameter = URLEncoder.encode("중구", "UTF-8");
            urlString  = urlString + koreanParameter;
        } catch (UnsupportedEncodingException e) { e.printStackTrace(); }

        // HttpURLConnection
        Http http = new Http();
        http.getWebData(this);

        // move the Camera Seoul
        LatLng seoul = new LatLng(37.566696, 126.977942);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String getUrl() {
        return urlString;
    }

    // Callback Method
    @Override
    public void call(String jsonString) {
        try {
            List<String> parkCode = new ArrayList<>();

            // 1. JSON Text 의 String 전체를 JSON Object 로 변환
            JSONObject jsonObject = new JSONObject(jsonString);

            // 2. jsonObject 에서 최상위 JSON Object 추출
            JSONObject rootObject = jsonObject.getJSONObject("SearchParkingInfoRealtime");

            // 3. 최상위 JSON Object 에서 Row(Record) JSON Array 추출
            JSONArray rows = rootObject.getJSONArray("row");
            int arrLength = rows.length();

            // 4. 반복문으로 JSON Array 안의 모든 JSON Object 추출
            for(int i=0; i < arrLength; i++) {
                JSONObject parkObject = rows.getJSONObject(i);

                // 5. JSON Object 에서 Key 로 데이터 추출
                String code = parkObject.getString("PARKING_CODE");

                if(parkCode.contains(code))
                   continue;
                parkCode.add(code);

                double lat = parkObject.getDouble("LAT");
                double lng = parkObject.getDouble("LNG");

                int capa = parkObject.getInt("CAPACITY");
                int cur = parkObject.getInt("CUR_PARKING");
                int space = capa - cur;

                LatLng park = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(park).title(space + " / " + capa));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

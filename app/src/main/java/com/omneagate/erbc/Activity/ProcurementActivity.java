package com.omneagate.erbc.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.android.Intents;
import com.omneagate.erbc.Dto.AadharDto;
import com.omneagate.erbc.R;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ProcurementActivity extends AppCompatActivity implements View.OnClickListener {


    private AadharDto aadharDto;
    Button scan;
    TextView scan_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurement_activity_new);
        aadharDto = new AadharDto();
        scan_text=(TextView)findViewById(R.id.scan_text);

        setUpView();

    }

    private void setUpView() {
        scan=(Button)findViewById(R.id.scan);
        scan.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.scan:
                if (ActivityCompat.checkSelfPermission(ProcurementActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has not been granted.
                    requestCameraPermission();
                } else {  }
                launchQRScanner();
                break;
        }
    }



    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
    }


    private void launchQRScanner() {
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String contents = data.getStringExtra(Intents.Scan.RESULT);
                        if (contents.contains("<PrintLetterBarcodeData")) {
                            String resultString = null;
                            StringBuilder sb = new StringBuilder(contents);
                            if ((sb.charAt(1) == '/')) {
                                sb.deleteCharAt(1);
                                resultString = sb.toString();
                            } else {
                                resultString = contents;
                            }
                            xmlParsing(resultString);
                        } else {
                            stringParsing(contents);
                        }
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(ProcurementActivity.class.getSimpleName(), "Scan cancelled");
                }
                break;

            default:
                break;
        }
    }




    private void xmlParsing(String xmlData) {
        try {
//            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called xmlData ->" + xmlData);
            String xmlRecords = xmlData;
            Log.e("xmlRecords",""+xmlData);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document dom = db.parse(is);
            NodeList l = dom.getElementsByTagName("PrintLetterBarcodeData");
            Log.e("xmlRecords",""+l.getLength());
            for (int j = 0; j < l.getLength(); ++j) {
                Node prop = l.item(j);
                NamedNodeMap attr = prop.getAttributes();
                if (null != attr) {
                    //Node nodeUid = attr.getNamedItem("uid");
                    String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                            state = "", pc = "", dob = "";
                    try {
                        uid = attr.getNamedItem("uid").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        name = attr.getNamedItem("name").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        gender = attr.getNamedItem("gender").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        yob = attr.getNamedItem("yob").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        co = attr.getNamedItem("co").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        house = attr.getNamedItem("house").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        street = attr.getNamedItem("street").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        lm = attr.getNamedItem("lm").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        loc = attr.getNamedItem("loc").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        vtc = attr.getNamedItem("vtc").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        po = attr.getNamedItem("po").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        dist = attr.getNamedItem("dist").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        subdist = attr.getNamedItem("subdist").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        state = attr.getNamedItem("state").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        pc = attr.getNamedItem("pc").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        dob = attr.getNamedItem("dob").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    aadharDto.setAadhaarNum(Long.parseLong(uid));
                    aadharDto.setUid(uid);
                    aadharDto.setName(name);
                    aadharDto.setGender(gender.charAt(0));
                    aadharDto.setCo(co);
                    aadharDto.setHouse(house);
                    aadharDto.setStreet(street);
                    aadharDto.setLm(lm);
                    aadharDto.setLoc(loc);
                    aadharDto.setVtc(vtc);
                    aadharDto.setPo(po);
                    aadharDto.setDist(dist);
                    aadharDto.setSubdist(subdist);
                    aadharDto.setState(state);
                    aadharDto.setPc(pc);
                    if (!yob.equalsIgnoreCase("")) {
                        aadharDto.setYob(Long.parseLong(yob));
                    }
                    try {
                        if (dob != null && !dob.isEmpty()) {
                            if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                                // Pattern dd/MM/yyyy
                                DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd/MM/yyyy ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());
                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                                // Pattern dd-MM-yyyy
                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd-MM-yyyy ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                                // Pattern yyyy/MM/dd
                                DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd MM yyyy ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy/MM/dd ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy-MM-dd ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy MM dd ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());
                                aadharDto.setDob(date1.getTime());
                            } else {
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob Unkown Pattern ->" + dob
//                                );
                            }
                        } else {
                            aadharDto.setDob(null);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        GregorianCalendar gc = new GregorianCalendar();
                        String date = sdf.format(gc.getTime());
                        Date createdDate = sdf.parse(date);
                        aadharDto.setCreatedDate(createdDate.getTime());
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);

                    }

                    Log.e("UIDValue", uid);
                    Log.e("name", name);
                    Log.e("gender", gender);
                    Log.e("yob", yob);
                    Log.e("co", co);
                    Log.e("house", house);
                    Log.e("street", street);
                    Log.e("lm", lm);
                    Log.e("loc", loc);
                    Log.e("vtc", vtc);
                    Log.e("po", po);
                    Log.e("dist", dist);
                    Log.e("subdist", subdist);
                    Log.e("state", state);
                    Log.e("pc", pc);
                    Log.e("dob", dob);
    //                scan_text.setText(""+uid+" "+name+" "+gender+" "+yob+" "+co+" "+house+" "+street+" "+lm+" "+loc+" "+vtc+" "+po);

                    String addr = "";
                    if (!house.equalsIgnoreCase("")) {
                        addr = house;
                    }
                    if (!street.equalsIgnoreCase("")) {
                        addr = addr + ", " + street;
                    }
                    if (!lm.equalsIgnoreCase("")) {
                        addr = addr + ", " + lm;
                    }
                    if (!loc.equalsIgnoreCase("")) {
                        addr = addr + ", " + loc;
                    }
                    if (!po.equalsIgnoreCase("")) {
                        addr = addr + ", " + po;
                    }
                    if (!subdist.equalsIgnoreCase("")) {
                        addr = addr + ", " + subdist;
                    }
                    if (!dist.equalsIgnoreCase("")) {
                        addr = addr + ", " + dist;
                    }
                    if (!state.equalsIgnoreCase("")) {
                        addr = addr + ", " + state;
                    }
                    if (!pc.equalsIgnoreCase("")) {
                        addr = addr + ", " + pc;
                    }
                    if (addr.startsWith(", ")) {
                        addr = addr.substring(2);
                    }
                    addr = addr + ".";
//                    addressTv.setText(addr);
                }
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }


    private void stringParsing(String text) {
        String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                state = "", pc = "", dob = "";
        String[] strArr = text.split(",");
        for (int i = 0; i < strArr.length; i++) {
            try {
                Log.e("AddFarmerActivity", "strArr contents" + strArr[i].toString());
                String element = strArr[i].toString();
                String[] strArr2 = element.split(":");

                if (strArr2[0].equalsIgnoreCase(" aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" DOB")) {
                    dob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("DOB")) {
                    dob = strArr2[1];
                }

            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("AddFarmerActivity", "string exception" + e);
            }
        }

        Log.e("THE AADHAR CARD NUMBER", "THE AADHAR " + uid);
        aadharDto.setAadhaarNum(Long.parseLong(uid));
        aadharDto.setUid(uid);
        aadharDto.setName(name);
        aadharDto.setGender(gender.charAt(0));
        aadharDto.setCo(co);
        aadharDto.setHouse(house);
        aadharDto.setStreet(street);
        aadharDto.setLm(lm);
        aadharDto.setLoc(loc);
        aadharDto.setVtc(vtc);
        aadharDto.setPo(po);
        aadharDto.setDist(dist);
        aadharDto.setSubdist(subdist);
        aadharDto.setState(state);
        aadharDto.setPc(pc);
        if (!yob.equalsIgnoreCase("")) {
            aadharDto.setYob(Long.parseLong(yob));
        }
        try {
            if (dob != null && !dob.isEmpty()) {
                if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                    // Pattern dd/MM/yyyy
                    DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd/MM/yyyy ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                    // Pattern dd-MM-yyyy
                    DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd-MM-yyyy ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                    // Pattern yyyy/MM/dd
                    DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd MM yyyy ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy/MM/dd ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy-MM-dd ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy MM dd ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else {
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob Unknown Pattern ->" + dob
//                    );
                }
            } else {
                aadharDto.setDob(null);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String date = sdf.format(gc.getTime());
            Date createdDate = sdf.parse(date);
            aadharDto.setCreatedDate(createdDate.getTime());

            Log.e("UIDValue", uid);
            Log.e("name", name);
            Log.e("gender", gender);
            Log.e("yob", yob);
            Log.e("co", co);
            Log.e("house", house);
            Log.e("street", street);
            Log.e("lm", lm);
            Log.e("loc", loc);
            Log.e("vtc", vtc);
            Log.e("po", po);
            Log.e("dist", dist);
            Log.e("subdist", subdist);
            Log.e("state", state);
            Log.e("pc", pc);
            Log.e(" dob", dob);


            String addr = "";
            if (!house.equalsIgnoreCase("")) {
                addr = house;
            }
            if (!street.equalsIgnoreCase("")) {
                addr = addr + ", " + street;
            }
            if (!lm.equalsIgnoreCase("")) {
                addr = addr + ", " + lm;
            }
            if (!loc.equalsIgnoreCase("")) {
                addr = addr + ", " + loc;
            }
            if (!po.equalsIgnoreCase("")) {
                addr = addr + ", " + po;
            }
            if (!subdist.equalsIgnoreCase("")) {
                addr = addr + ", " + subdist;
            }
            if (!dist.equalsIgnoreCase("")) {
                addr = addr + ", " + dist;
            }
            if (!state.equalsIgnoreCase("")) {
                addr = addr + ", " + state;
            }
            if (addr.startsWith(", ")) {
                addr = addr.substring(1);
            }
            if (!pc.equalsIgnoreCase("")) {
                addr = addr + ", " + pc;
            }
            if (addr.startsWith(", ")) {
                addr = addr.substring(2);
            }
            addr = addr + ".";

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
        }
    }

    /*private void startListActivity() {
        Log.e("checkValidAadharCard", "*************" + aadharDto.getUid());
        if (networkConnection.isNetworkAvailable()) {
            Log.e("Online Procurement", "*************");
            try {
                aadhaar = "t";
                HashMap<String, Object> inputParams = Utilities.getInstance().getRequestParams();
                inputParams.put("aadhaarNumber", aadharDto.getUid());
                Object data = "";
                controller.handleMessage(ResReqController.SEARCH_FARMER_ONLINE, inputParams, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Offline Procurement", "*************");
            FarmerRegistrationDto farmer_list = DBHelper.getInstance(this).getFarmerDetailsProcurement(aadharDto.getUid());
            Log.e("FARMER ID",""+farmer_list.getId());
            aadhaar ="f";
            if (farmer_list != null) {
                Intent p_farmer_list = new Intent(ProcurementActivity.this, ProcurementDetailsActivity.class);
                p_farmer_list.putExtra("farmerRegistrationDto", farmer_list);
                startActivity(p_farmer_list);
                finish();
            } else {
                showToastMessage(getString(R.string.toast_aadhar_number_not_found), 3000);
            }
        }
    }*/

   /* private void checkFarmercode(String farmer_code) {
        if (farmer_code.isEmpty()) {
            showToastMessage(getString(R.string.toast_enter_farmer_code), 3000);
            return;
        } else if (networkConnection.isNetworkAvailable()) {
            Log.e("Online Procurement", "*************");
            try {
                farmer_code_="t";
                HashMap<String, Object> inputParams = Utilities.getInstance().getRequestParams();
                inputParams.put("farmerCode", farmer_code);
                Object data = "";
                controller.handleMessage(ResReqController.SEARCH_FARMER_ONLINE, inputParams, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FarmerRegistrationDto farmer_registration_dto = DBHelper.getInstance(this).getFarmerDetailsProcurement(farmer_code);
            farmer_code_="f";
            if (farmer_registration_dto != null) {
                Intent p_farmer_list = new Intent(ProcurementActivity.this, ProcurementDetailsActivity.class);
                p_farmer_list.putExtra("farmerRegistrationDto", farmer_registration_dto);
                startActivity(p_farmer_list);
                finish();
            } else {
                showToastMessage(getString(R.string.toast_farmer_code_not_found), 3000);
            }
            Log.e("THE FARMER LIST TO", "&&&&&&&&&&&&&" + farmer_registration_dto);
        }

    }*/


}

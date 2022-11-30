package app.orosoft.safaricom;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.profile_image).startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));

    }

    public void openAccounts(View v) {
        switch (v.getId()) {
            case R.id.goto_fb:
                openLink("https://www.facebook.com/fuadfmb");
                break;
            case R.id.goto_fbpage :
            case R.id.openfb :
                openLink("https://www.facebook.com/orosoft");
                break;
            case R.id.goto_play :
                rateUs();
                break;
            case R.id.goto_privacy :
                new AlertDialog.Builder(this).setTitle((CharSequence) "Privacy policy").setMessage((CharSequence) Html.fromHtml(getPolicy())).setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
                break;
            case R.id.goto_tgchannel :
            case R.id.opentg :
                openLink("https://t.me/orosoft");
                break;
            case R.id.openyt :
                openLink("https://youtube.com/channel/UCPACDh_KISHZyhj27nMjiQA");
                break;
        }
    }

    public void openLink(String url) {
        startActivity(Intent.createChooser(new Intent("android.intent.action.VIEW", Uri.parse(url)), "Browse with"));
    }

    public void rateUs() {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public String getPolicy() {
        return "<p>\nThis privacy policy applies to "+getString(R.string.app_name)+" android Application.\nyou agree to this privacy policy by installing and using this Application. \nplease do not install or use this Application if you do not agree with this privacy policy.\n</p>\n<p>\nThis page is used to inform visitors regarding our policies with the collection, use, \nand disclosure of Personal Information if anyone decided to use our Service.\nIf you choose to use our Service, then you agree to the collection and use of \ninformation in relation to this policy.\n</p>\n\n<h2>Information Collection and Use</h2>\n<p>\nwe do not collect or use any personal data from users.\nthe Application does not send any information to Orosoft Technologies without users knowledge.\nif you send us feedback or question via email, \nyour email address is visible to us and we may contact you directly through that email if necessary. \n</p>\n\n<p>\nwe do not ask any personal information such as:- your name, country, place of birth,\nemail address, photos, phone number and etc from our customers.\n\n\n</p>\n\n\n<h2>Children's Privacy</h2>\n<p>\nThese Services do not address anyone under \nthe age of 13. We do not knowingly collect \npersonally identifiable information from children \nunder 13.\n</p>\n\n<h2>Changes to This Privacy Policy</h2>\n<p>\nWe may update our Privacy Policy from time to time. \nThus, you are advised to review <a href=\"https://orosoftblog.blogspot.com/p/seerlugaa-privacy-policy.html\">this page</a> periodically\nfor any changes. We will notify you of any changes by \nposting the new Privacy Policy on this page. These \nchanges are effective immediately after they are posted \non this page.\n</p>\n\n<h2>Links to Other Sites</h2>\n<p>\nThis Service may contain links to other sites. \nIf you click on a third-party link, you will \nbe directed to that site. Note that these \nexternal sites are not operated by us. \nTherefore, we strongly advise you to review \nthe Privacy Policy of these websites. \nWe have no control over and assume no \nresponsibility for the content, privacy \npolicies, or practices of any third-party sites or services.\n\n\n<h2>Contact Us</h2>\n<p>\nIf you have any questions or suggestions about our \nPrivacy Policy, do not hesitate to contact us.<br>\nEmail : fuadmoh9@gmail.com\n\n</p>";
    }

    public void openApp(View v) {
        String packageName = "";
        if (v.getId() == R.id.seerlugaa) {
            packageName = "app.orosoft.seerlugaa";
        } else if (v.getId() == R.id.telecard) {
            packageName = "app.orosoft.telecard";
        } else if (v.getId() == R.id.ororo) {
            packageName = "app.orosoft.ororo";
        }
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
    }

}


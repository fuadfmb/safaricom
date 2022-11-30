package app.orosoft.safaricom;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

import static android.app.Activity.RESULT_OK;


public class DialogCallmeBack extends DialogFragment {

    public static final String TAG = "clb_dialog";
    private Toolbar toolbar;
    EditText et_clb_number;
    ImageButton btn_pick;
    AppCompatButton btn_req_cmb;
    ConstraintLayout container;
    InterstitialAd mInterstitialAd;

    public static DialogCallmeBack display(FragmentManager fragmentManager) {
        DialogCallmeBack exampleDialog = new DialogCallmeBack();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MainTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.MainTheme_Slide);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( mInterstitialAd != null ) {
            mInterstitialAd.show(getActivity());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.callmeback_dialog, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        et_clb_number = view.findViewById(R.id.et_clb_number);
        btn_pick = view.findViewById(R.id.btn_pick);
        btn_req_cmb = view.findViewById(R.id.btn_req_cmb);
        container = view.findViewById(R.id.container);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /////////////////////////////////////////////////////////////////// BANNER AD
        MobileAds.initialize(getActivity(), initializationStatus -> { });
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        /////////////////////////////////////////////////////////////////// BANNER AD

        /////////////////////////////////////////////////////////////////// Interstitial
        AdRequest inter = new AdRequest.Builder().build();
        InterstitialAd.load(getActivity(), getString(R.string.ad_interstitial), inter,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }
                });
        /////////////////////////////////////////////////////////////////// Interstitial


        toolbar.setNavigationOnClickListener(v -> dismiss());

        btn_req_cmb.setOnClickListener(view1 -> {
            String input = et_clb_number.getText().toString().trim();
            if ( input.length() == 10 ) {
                call( "*707*" + input + "#" );
            }
            else{
                Snackbar.make( view, getString(R.string.jv_inv_data), Snackbar.LENGTH_SHORT )
                    .setAction(getString(R.string.why), view2 ->
                        new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.error))
                        .setMessage(Html.fromHtml(
                                getString(R.string.jv_ph_len_error)
                        ))
                        .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setCancelable(true)
                        .create()
                        .show())
                    .show();
            }
        });

        btn_pick.setOnClickListener(view12 -> {

            if (ActivityCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED ) {
                String[] perms = {
                        Manifest.permission.READ_CONTACTS,
                };
                ActivityCompat.requestPermissions(getActivity(), perms, 345786 );
                Toast.makeText(getActivity(), getString(R.string.grant_perms), Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 101);
            }

        });
    }

    // make a call
    public void call(String ussd) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED ) {
                String[] perms = {
                        Manifest.permission.CALL_PHONE,
                };
                ActivityCompat.requestPermissions(getActivity(), perms, 3456 );
                Toast.makeText(getActivity(), getString(R.string.grant_perms), Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Uri.encode( ussd )) );
                startActivity( intent );
            }
        }
        catch (Exception ignored){ }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == 101 ) {
            if ( resultCode == RESULT_OK ) {
                try {
                    Cursor c = getActivity().getContentResolver().query(data.getData(), null, null,null,null);
                    if ( c.moveToNext() ) {
                        int x = c.getColumnIndex( ContactsContract.Contacts._ID );
                        String contactID = c.getString( x );
//                        int numberColumnIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                        String number = c.getString(numberColumnIndex); // error occurs here !!
                        int y = c.getColumnIndex(String.valueOf(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                        String idResults = c.getString( y );
                        int idResultHold = Integer.parseInt( idResults );


                        if (idResultHold == 1){
                            Cursor c2 = getActivity().getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" "+contactID,
                                    null,
                                    null
                                    );
                            while ( c2.moveToNext() ) {
                                int z = c2.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER );
                                String contactNumber = c2.getString( z );
                                // formatted number
                                String formattedNum = contactNumber.trim()
                                        .replace("+2510", "0")
                                        .replace("+251", "0");
                                et_clb_number.setText( formattedNum );
                            }

                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


package app.orosoft.safaricom;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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


public class DialogRecharge extends DialogFragment {

    public static final String TAG = "recharge_dialog";
    private Toolbar toolbar;
    SwitchCompat giftswitch;
    ConstraintLayout giftarea;
    EditText et_card_number, et_phone_number;
    ImageButton btn_pick;
    AppCompatButton btn_read_card, btn_recharge;
    InterstitialAd mInterstitialAd;

    private static final int CAMERA_REQUEST_CODE = 12345;

    public static DialogRecharge display(FragmentManager fragmentManager) {
        DialogRecharge exampleDialog = new DialogRecharge();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.recharge_dialog, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        giftswitch = view.findViewById(R.id.giftswitch);
        giftarea = view.findViewById(R.id.giftarea);
        et_card_number = view.findViewById(R.id.et_card_number);
        et_phone_number = view.findViewById(R.id.et_phone_number);
        btn_pick = view.findViewById(R.id.btn_pick);
        btn_read_card = view.findViewById(R.id.btn_read_card);
        btn_recharge = view.findViewById(R.id.btn_recharge);

        // hide gift area
        giftarea.setVisibility(View.GONE);

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
        giftswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if ( ! checked ) {
                    giftarea.setVisibility(View.GONE);
                }
                else{
                    giftarea.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_read_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        btn_recharge.setOnClickListener(view1 -> {
            String input_mcard = et_card_number.getText().toString().trim();
            if (giftswitch.isChecked()){
                String input_phone = et_phone_number.getText().toString().trim();
                if (input_phone.length() == 10 && input_mcard.length() == 14) {
                    call( "*705*2*"+input_phone+"*"+input_mcard+"*#" );
                }
                else{
                    Snackbar.make( view, getString(R.string.jv_inv_data), Snackbar.LENGTH_SHORT )
                        .setAction(getString(R.string.why), view2 -> new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.error))
                            .setMessage(Html.fromHtml(
                                    getString(R.string.jv_ph_card_len_error)
                            ))
                            .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setCancelable(true)
                            .create()
                            .show())
                        .show();
                }
            }
            else{
                if ( input_mcard.length() == 14 ){
                    call( "*705*1*"+input_mcard+"#" );
                }
                else{
                    Snackbar.make( view, getString(R.string.jv_inv_data), Snackbar.LENGTH_SHORT )
                        .setAction(getString(R.string.why), view2 -> new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.error))
                            .setMessage(Html.fromHtml(
                                    getString(R.string.jv_card_len_error)
                            ))
                            .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setCancelable(true)
                            .create()
                            .show())
                        .show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ) {

            String cardNum = data.getStringExtra("cardnum").trim().replace(" ", "");
            if ( cardNum.matches("[0-9]+") ) {
                et_card_number.setText( cardNum );
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.digits_only), Toast.LENGTH_LONG).show();
                et_card_number.setText( "" );
            }

        }

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

}
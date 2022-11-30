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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
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


public class DialogTransfer extends DialogFragment {

    public static final String TAG = "trans_dialog";
    private Toolbar toolbar;
    EditText et_trans_number, et_amount;
    ImageButton btn_pick;
    AppCompatButton btn_transfer;
    InterstitialAd mInterstitialAd;

    public static DialogTransfer display(FragmentManager fragmentManager) {
        DialogTransfer exampleDialog = new DialogTransfer();
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
        View view = inflater.inflate(R.layout.transfer_dialog, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        et_trans_number = view.findViewById(R.id.et_trans_number);
        et_amount = view.findViewById(R.id.et_amount);
        btn_pick = view.findViewById(R.id.btn_pick);
        btn_transfer = view.findViewById(R.id.btn_transfer);

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

        btn_transfer.setOnClickListener(view1 -> {
            String trans_phone  = et_trans_number.getText().toString().trim();
            String trans_amount = et_amount.getText().toString().trim();
            if (trans_amount.length() != 0 && Integer.parseInt( trans_amount ) > 0 && trans_phone.length() == 10) {
                call( "*706*"+trans_phone+"*"+trans_amount+"#" );
            }
            else {
                Snackbar.make( view, getString(R.string.jv_inv_data), Snackbar.LENGTH_SHORT )
                    .setAction(getString(R.string.why), view2 -> new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.error))
                        .setMessage(Html.fromHtml(
                                getString(R.string.jv_ph_amount_error)
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
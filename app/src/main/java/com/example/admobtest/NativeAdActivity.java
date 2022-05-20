package com.example.admobtest;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class NativeAdActivity extends AppCompatActivity {


    /*
    In coding terms, this means that when a native ad loads, your app receives a NativeAd object that contains its assets, and the app (rather than the Google Mobile Ads SDK) is then responsible for displaying them.
    *
    * two things in implementation :
    * - loading ad via SDK
    * - displaying the ad in app
    *
    * prereq :
    * - import SDK either by itself or Firebase
    *
    *
     */

    /*
    Native ads are loaded via the AdLoader class, which has its own Builder class to customize it during creation. By adding listeners to the AdLoader while building it, an app specifies which types of native ads it is ready to receive. The AdLoader then requests just those types.
     */

    /*
    Applications displaying native ads are free to request them in advance of when they'll actually be displayed. In many cases, this is the recommended practice. An app displaying a list of items with native ads mixed in, for example, can load native ads for the whole list, knowing that some will be shown only after the user scrolls the view and some may not be displayed at all.
     */

    /*
    The ad view classes also provide methods used to register the view used for each individual asset, and one to register the NativeAd object itself. Registering the views in this way allows the SDK to automatically handle tasks such as:

Recording clicks
Recording impressions (when the first pixel is visible on the screen)
Displaying the AdChoices overlay
     */

    /*
    AdChoices Overlay and Ad Attribution
    AdChoices overlay
An AdChoices overlay is added to each ad view by the SDK. Leave space in your preferred corner of your native ad view for the automatically inserted AdChoices logo. Also, it's important that the AdChoices overlay be easily seen, so choose background colors and images appropriately. For more information on the overlay's appearance and function, see Native ads advanced field descriptions.

Ad attribution
You must display an ad attribution to denote that the view is an advertisement. Learn more in our policy guidelines.
     */

    private static String testCode = "ca-app-pub-3940256099942544/2247696110";
    private AdLoader loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nativead);
        //adloader selects the ad that maximise yield
        loader = new AdLoader.Builder(getApplicationContext(), testCode).forNativeAd(nativeAd -> {
            //show the ad here
            //this function prepares the ad for the nativead format
            //configures the adloader to request native ads
            //returns a nativeAd object after it laods so you can use it

            //use the framelayout as placeholder for the ad (example)
            FrameLayout layout = findViewById(R.id.nativeAdPlaceholder);
            NativeAdView nativeAdView = (NativeAdView) getLayoutInflater().inflate(R.layout.nativead, null);

            //assume this method sets the text, image and native ad into the native ad
            populateNativeAd(nativeAdView, nativeAd);
            layout.removeAllViews();
            layout.addView(nativeAdView);

            if(isDestroyed()){
                nativeAd.destroy();
            }
        }).withAdListener(new AdListener() {
            //adlistener is optional
            //
            @Override
            public void onAdClicked() {
                //handle what happens when the ad is clicked
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                //handle what happens when the ad is closed
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                //handle what happens when the ad fails to load
                //you should not attempt to reload a new ad here, discouraged
                //limit if you have to to avoid continuous failed ad requests ie connection problems
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                //handle what happens after an impression is recorded
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                //handle what happens after ad is loaded and ready to be shown
                //call AdLoader.isLoading to confirm if laoding has finished or not
                if(!loader.isLoading()){
                    Toast.makeText(getApplicationContext(), "Ad has finished loading", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "App is currently loading your ad ", Toast.LENGTH_SHORT).show();
                }
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                //handle what happens while ad is opened
                super.onAdOpened();
            }
        }).withNativeAdOptions(new NativeAdOptions.Builder().build()).build();
        /*
        after done building, you can load with loadAd or loadAds
        loadAd = load a single ad
        loadAds() = load few ads up to 5
        2nd parameter is not guaranteed
        all ads requested will be different from each other

         */
        loader.loadAds(new AdRequest.Builder().build(), 5);
    }

    private void populateNativeAd(NativeAdView adView, NativeAd nativeAd) {
        Toast.makeText(this, "Native ad populated into the view", Toast.LENGTH_SHORT).show();
    }

    //example function of an adview display
    private void displayNativeAd(ViewGroup parent, NativeAd ad){
        LayoutInflater inflater =  (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NativeAdView adView = (NativeAdView) inflater.inflate(R.layout.nativead, parent);
        MediaView view = (MediaView) adView.findViewById(R.id.mediaView);
        /*
        create elements for each items for native ad then assign
         */
        /*
        adView.setMediaView(view); *
        adView.setHeadlineView();*
        adView.setBodyView();*
        adView.setCallToActionView(); *
        adView.setIconView();
        adView.setPriceView();
        adView.setStarRatingView();
        adView.setStoreView();
        adView.setAdvertiserView();
        * - required
         */

    }

}

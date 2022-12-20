package com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.R
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.*

class AdUtils {

    companion object{

        var userActionsCount = 1
        var shouldShowAd = true
        private val AD_INTERVAL = 4

        fun adShown() {
            shouldShowAd = false
        }

        fun shouldShowAd(): Boolean {
            return shouldShowAd
        }

        fun incrementUserAction() {
            userActionsCount++
            if (userActionsCount % AD_INTERVAL == 0) {
                shouldShowAd = true
            }
        }

        // to avoid double increments for ad showing
        fun adjustUserAction() {
            userActionsCount--
        }


        fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            (Objects.requireNonNull(adView.headlineView) as TextView).text =
                nativeAd.headline
            Objects.requireNonNull(adView.mediaView)!!.mediaContent =
                Objects.requireNonNull(nativeAd.mediaContent)

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView!!.visibility = View.INVISIBLE
            } else {
                adView.bodyView!!.visibility = View.VISIBLE
                (adView.bodyView as TextView?)!!.text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                adView.callToActionView!!.visibility = View.INVISIBLE
            } else {
                adView.callToActionView!!.visibility = View.VISIBLE
                (adView.callToActionView as Button?)!!.text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                adView.iconView!!.visibility = View.GONE
            } else {
                (adView.iconView as ImageView?)!!.setImageDrawable(
                    nativeAd.icon!!.drawable
                )
                adView.iconView!!.visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                adView.priceView!!.visibility = View.GONE
            } else {
                adView.priceView!!.visibility = View.VISIBLE
                (adView.priceView as TextView?)!!.text = nativeAd.price
            }
            if (nativeAd.store == null) {
                adView.storeView!!.visibility = View.GONE
            } else {
                adView.storeView!!.visibility = View.VISIBLE
                (adView.storeView as TextView?)!!.text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                adView.starRatingView!!.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar?)!!.rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView!!.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                adView.advertiserView!!.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView?)!!.text = nativeAd.advertiser
                adView.advertiserView!!.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)
        }


    }

}
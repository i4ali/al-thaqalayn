package com.thaqalayn.app.premium

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

/**
 * Google Play Billing for the one-time premium unlock (the Android counterpart
 * of the iOS StoreKit PurchaseManager). Product: com.thaqalayn.premium.tafsir.
 */
object BillingManager : PurchasesUpdatedListener {
    const val PRODUCT_ID = "com.thaqalayn.premium.tafsir"

    private var billingClient: BillingClient? = null
    private var productDetails: ProductDetails? = null

    var isConnected by mutableStateOf(false)
        private set
    var priceText by mutableStateOf<String?>(null)
        private set
    var purchaseError by mutableStateOf<String?>(null)
    var purchaseSuccess by mutableStateOf(false)
    var isPurchasing by mutableStateOf(false)
        private set

    fun init(context: Context) {
        billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(this)
            .enableAutoServiceReconnection()
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build()
        connect()
    }

    private fun connect() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    isConnected = true
                    queryProduct()
                    restorePurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
            }
        })
    }

    private fun queryProduct() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        billingClient?.queryProductDetailsAsync(params) { result, productDetailsResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetails = productDetailsResult.productDetailsList.firstOrNull()
                priceText = productDetails?.oneTimePurchaseOfferDetails?.formattedPrice
            }
        }
    }

    /** Re-checks owned purchases; also grants premium bought on another device. */
    fun restorePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient?.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val owned = purchases.any {
                    it.products.contains(PRODUCT_ID) && it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                if (owned) PremiumManager.updatePremium(true)
                purchases.forEach { acknowledgeIfNeeded(it) }
            }
        }
    }

    fun purchase(activity: Activity) {
        val details = productDetails
        if (details == null) {
            purchaseError = "Product not available. Please try again."
            queryProduct()
            return
        }
        isPurchasing = true
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .build()
                )
            )
            .build()
        billingClient?.launchBillingFlow(activity, params)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        isPurchasing = false
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    if (purchase.products.contains(PRODUCT_ID) &&
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                    ) {
                        PremiumManager.updatePremium(true)
                        purchaseSuccess = true
                        acknowledgeIfNeeded(purchase)
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> Unit
            else -> purchaseError = "Purchase failed. Please try again."
        }
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient?.acknowledgePurchase(params) { }
        }
    }
}

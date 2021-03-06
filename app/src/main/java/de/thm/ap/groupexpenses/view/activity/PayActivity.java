package de.thm.ap.groupexpenses.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.HashMap;
import java.util.Map;

import de.thm.ap.groupexpenses.R;

public class PayActivity extends AppCompatActivity {

    private final String successful = "Braintree"; // payment method nonce
    final int REQUEST_CODE = 1;
    final String getToken = "http://expenses.deneb.uberspace.de/main.php";
    final String payDetails = "http://expenses.deneb.uberspace.de/checkout.php";
    String token, amount;
    HashMap<String, String> paramHash;
    private Bundle extras;

    Button btnPay;
    TextView tVAmount;
    LinearLayout llHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        llHolder = findViewById(R.id.llHolder);
        tVAmount = findViewById(R.id.tVPrice);
        btnPay = findViewById(R.id.btnPay);
        extras = getIntent().getExtras();
        String payThis = extras.getString("amount");
        tVAmount.setText(payThis + "€");
        btnPay.setOnClickListener(v -> onBraintreeSubmit());
        new HttpRequest().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String stringNonce = nonce.getNonce();
                Log.d("mylog", "Result: " + stringNonce);
                // Send payment price with the nonce
                // use the result to update your UI and send the payment method nonce to your server
                if (!tVAmount.getText().toString().isEmpty()) {
                    amount = tVAmount.getText().toString().replace("€", "");
                    paramHash = new HashMap<>();
                    paramHash.put("amount", amount);
                    paramHash.put("nonce", stringNonce);
                    sendPaymentDetails();
                } else
                    Toast.makeText(PayActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user canceled");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("mylog", "Error : " + error.toString());
            }
        }
    }

    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void sendPaymentDetails() {
        RequestQueue queue = Volley.newRequestQueue(PayActivity.this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, payDetails,
                response -> {
                    Intent returnIntent = new Intent();
                    String debtor_uid = extras.getString("debtor_uid");
                    returnIntent.putExtra("debtor_uid", debtor_uid);
                    if (response.contains(successful)) {
                        setResult(Activity.RESULT_OK, returnIntent);
                    } else {
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                    }
                    Log.d("mylog", "Final Response: " + response);
                    finish();
                }, error -> Log.d("mylog", "Volley error : " + error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private class HttpRequest extends AsyncTask {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(PayActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
            progress.setCancelable(false);
            progress.setMessage("We are contacting our servers for token, Please wait");
            progress.setTitle("Getting token");
            progress.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(getToken, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {
                    Log.d("mylog", responseBody);
                    runOnUiThread(() -> {
                        Toast.makeText(PayActivity.this, "Successfully got token", Toast.LENGTH_SHORT).show();
                        llHolder.setVisibility(View.VISIBLE);
                    });
                    token = responseBody;
                }

                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(() -> Toast.makeText(PayActivity.this, "Failed to get token: " + ex.toString(), Toast.LENGTH_LONG).show());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
        }
    }
}

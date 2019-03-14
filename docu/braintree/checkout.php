<?php
require_once ("include/braintree_init.php");
require_once ("vendor/braintree/braintree_php/lib/Braintree.php");

$nonce = $_POST['nonce'];
$amount = $_POST['amount'];

/*$result = Braintree_Transaction::sale([
  'amount' => $amount,
  'paymentMethodNonce' => $nonce,
  'options' => [
    'submitForSettlement' => True
  ]
]);*/
$result = $gateway->transaction()->sale([
    'amount' => $amount,
    'paymentMethodNonce' => $nonce,
    'options' => [
        'submitForSettlement' => true
    ]
]);

echo $result;
?>
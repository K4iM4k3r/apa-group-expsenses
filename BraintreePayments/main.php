<?php 
require_once ("include/braintree_init.php");
require_once 'vendor/braintree/braintree_php/lib/Braintree.php';
echo($clientToken = $gateway->clientToken()->generate());
?>
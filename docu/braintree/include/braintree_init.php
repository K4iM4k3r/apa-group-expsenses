<?php
session_start();
require_once("vendor/autoload.php");
if(file_exists(__DIR__ . "/../.env")) {
    $dotenv = new Dotenv\Dotenv(__DIR__ . "/../");
    $dotenv->load();
}
$gateway = new Braintree_Gateway([
    'environment' => 'sandbox',
    'merchantId' => 'd7d2dnd8qytbg7zd',
    'publicKey' => 'jn4s9s87cvd4kts9',
    'privateKey' => '5a868a99a63cc8e5fa4e52287dc2d597'
]);
?>

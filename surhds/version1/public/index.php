<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

//require '../vendor/autoload.php';
require __DIR__ . '/../vendor/autoload.php';
require __DIR__ . '/../src/config/db.php';

$app = new \Slim\App;
$app->get('/hello/{name}', function (Request $request, Response $response, array $args) {
    $user = $request->getAttribute('name');
    $response->getBody()->write("username, $user");

    $name = $args['name'];
    $response->getBody()->write("Hello, $name");

    return $response;
});

//customer Routes
require __DIR__ . '/../src/routes/customers.php';
$app->run();

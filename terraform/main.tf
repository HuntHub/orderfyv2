module "api_gateway" {
  source     = "./modules/api_gateway"
  lambda_arn = module.lambda.lambda_arn
}

module "lambda" {
  source = "./modules/lambda"
}

module "dynamodb" {
  source = "./modules/dynamodb"
}

module "s3" {
  source = "./modules/s3"
}
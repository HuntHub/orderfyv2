resource "aws_lambda_function" "my_lambda" {
  function_name    = "OrderfySpringLambda"
  handler          = "com.example.MyHandler::handleRequest"
  runtime          = "java17"
  filename         = "../backend/target/OrderfyApplication.jar"
  source_code_hash = filebase64sha256("../backend/target/OrderfyApplication.jar")

  role = aws_iam_role.lambda_role.arn

  environment {
    variables = {
      ENV_VAR_NAME = "value"
    }
  }
}

resource "aws_iam_role" "lambda_role" {
  name               = "lambda_execution_role"
  assume_role_policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
  inline_policy {
    name = "lambda_policy"
    policy = data.aws_iam_policy_document.lambda_policy.json
  }
}

data "aws_iam_policy_document" "lambda_policy" {
  statement {
    actions   = ["dynamodb:PutItem", "dynamodb:GetItem", "dynamodb:Query", "dynamodb:UpdateItem"]
    resources = ["arn:aws:dynamodb:*:*:table/orders"]
  }
}
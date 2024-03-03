resource "aws_dynamodb_table" "orders" {
  name           = "orders"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "vendorId"

  attribute {
    name = "vendorId"
    type = "S"
  }

  attribute {
    name = "orderId"
    type = "S"
  }

  attribute {
    name = "orderStatus"
    type = "S"
  }

  global_secondary_index {
    name               = "orderIdIndex"
    hash_key           = "orderId"
    projection_type    = "ALL"
    write_capacity     = 5
    read_capacity      = 5
  }

  global_secondary_index {
    name               = "OrderStatusIndex"
    hash_key           = "orderStatus"
    projection_type    = "ALL"
    write_capacity     = 5
    read_capacity      = 5
  }
}
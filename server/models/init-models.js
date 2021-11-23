var DataTypes = require("sequelize").DataTypes;
var _nutrition = require("./nutrition");
var _products = require("./products");
var _review = require("./review");
var _sales = require("./sales");
var _user = require("./user");

function initModels(sequelize) {
  var nutrition = _nutrition(sequelize, DataTypes);
  var products = _products(sequelize, DataTypes);
  var review = _review(sequelize, DataTypes);
  var sales = _sales(sequelize, DataTypes);
  var user = _user(sequelize, DataTypes);

  nutrition.belongsTo(products, { as: "prod", foreignKey: "prod_id"});
  products.hasMany(nutrition, { as: "nutritions", foreignKey: "prod_id"});
  sales.belongsTo(products, { as: "prod", foreignKey: "prod_id"});
  products.hasMany(sales, { as: "sales", foreignKey: "prod_id"});
  review.belongsTo(sales, { as: "sal", foreignKey: "sal_id"});
  sales.hasMany(review, { as: "reviews", foreignKey: "sal_id"});
  review.belongsTo(user, { as: "user", foreignKey: "user_id"});
  user.hasMany(review, { as: "reviews", foreignKey: "user_id"});

  return {
    nutrition,
    products,
    review,
    sales,
    user,
  };
}
module.exports = initModels;
module.exports.initModels = initModels;
module.exports.default = initModels;

const Sequelize = require('sequelize');
module.exports = function(sequelize, DataTypes) {
  return sequelize.define('products', {
    prod_id: {
      autoIncrement: true,
      type: DataTypes.INTEGER,
      allowNull: false,
      primaryKey: true
    },
    prod_img: {
      type: DataTypes.STRING(150),
      allowNull: false
    },
    prod_name: {
      type: DataTypes.STRING(30),
      allowNull: false,
      unique: "prod_name"
    },
    prod_category: {
      type: DataTypes.STRING(20),
      allowNull: false
    },
    prod_price: {
      type: DataTypes.INTEGER,
      allowNull: false
    }
  }, {
    sequelize,
    tableName: 'products',
    timestamps: false,
    indexes: [
      {
        name: "PRIMARY",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "prod_id" },
        ]
      },
      {
        name: "prod_name",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "prod_name" },
        ]
      },
    ]
  });
};

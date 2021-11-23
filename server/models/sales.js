const Sequelize = require('sequelize');
module.exports = function(sequelize, DataTypes) {
  return sequelize.define('sales', {
    sal_id: {
      autoIncrement: true,
      type: DataTypes.INTEGER,
      allowNull: false,
      primaryKey: true
    },
    store: {
      type: DataTypes.STRING(20),
      allowNull: false
    },
    prod_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: 'products',
        key: 'prod_id'
      }
    },
    saletype: {
      type: DataTypes.STRING(10),
      allowNull: false
    }
  }, {
    sequelize,
    tableName: 'sales',
    timestamps: false,
    indexes: [
      {
        name: "PRIMARY",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "sal_id" },
        ]
      },
      {
        name: "store",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "store" },
          { name: "prod_id" },
        ]
      },
      {
        name: "prod_id",
        using: "BTREE",
        fields: [
          { name: "prod_id" },
        ]
      },
    ]
  });
};

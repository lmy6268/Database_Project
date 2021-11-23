const Sequelize = require('sequelize');
module.exports = function(sequelize, DataTypes) {
  return sequelize.define('nutrition', {
    id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      primaryKey: true
    },
    kcal: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    tan: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    sugar: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    protein: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    fat: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    fofat: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    transfat: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    coles: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    nat: {
      type: DataTypes.DECIMAL(3,1),
      allowNull: false
    },
    prod_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: 'products',
        key: 'prod_id'
      }
    }
  }, {
    sequelize,
    tableName: 'nutrition',
    timestamps: false,
    indexes: [
      {
        name: "PRIMARY",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "id" },
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

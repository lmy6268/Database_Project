const Sequelize = require('sequelize');
module.exports = function(sequelize, DataTypes) {
  return sequelize.define('review', {
    review_id: {
      autoIncrement: true,
      type: DataTypes.INTEGER,
      allowNull: false,
      primaryKey: true
    },
    sal_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: 'sales',
        key: 'sal_id'
      }
    },
    user_id: {
      type: DataTypes.STRING(20),
      allowNull: false,
      references: {
        model: 'user',
        key: 'user_id'
      }
    },
    content: {
      type: DataTypes.STRING(100),
      allowNull: false
    },
    rate: {
      type: DataTypes.DECIMAL(2,1),
      allowNull: true
    }
  }, {
    sequelize,
    tableName: 'review',
    timestamps: false,
    indexes: [
      {
        name: "PRIMARY",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "review_id" },
        ]
      },
      {
        name: "sal_id",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "sal_id" },
          { name: "user_id" },
        ]
      },
      {
        name: "user_id",
        using: "BTREE",
        fields: [
          { name: "user_id" },
        ]
      },
    ]
  });
};

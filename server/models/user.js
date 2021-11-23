const Sequelize = require('sequelize');
module.exports = function(sequelize, DataTypes) {
  return sequelize.define('user', {
    user_id: {
      type: DataTypes.STRING(20),
      allowNull: false,
      primaryKey: true
    },
    user_pwd: {
      type: DataTypes.STRING(257),
      allowNull: false
    },
    user_name: {
      type: DataTypes.STRING(20),
      allowNull: false,
      unique: "user_name"
    },
    user_email: {
      type: DataTypes.STRING(50),
      allowNull: true,
      unique: "user_email"
    }
  }, {
    sequelize,
    tableName: 'user',
    timestamps: false,
    indexes: [
      {
        name: "PRIMARY",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "user_id" },
        ]
      },
      {
        name: "user_name",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "user_name" },
        ]
      },
      {
        name: "user_email",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "user_email" },
        ]
      },
    ]
  });
};

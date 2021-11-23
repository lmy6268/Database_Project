const express= require('express');
var db = require('../models');
console.log(db['products'].findAll());
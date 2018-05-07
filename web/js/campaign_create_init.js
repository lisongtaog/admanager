  var firstInitForm = false;

var regionList = [{"name":"Worldwide"},{"key":"AD","name":"Andorra","type":"country","country_code":"AD","supports_region":true,"supports_city":false},{"key":"AE","name":"United Arab Emirates","type":"country","country_code":"AE","supports_region":true,"supports_city":true},{"key":"AF","name":"Afghanistan","type":"country","country_code":"AF","supports_region":true,"supports_city":false},{"key":"AG","name":"Antigua","type":"country","country_code":"AG","supports_region":true,"supports_city":false},{"key":"AI","name":"Anguilla","type":"country","country_code":"AI","supports_region":true,"supports_city":false},{"key":"AL","name":"Albania","type":"country","country_code":"AL","supports_region":true,"supports_city":false},{"key":"AM","name":"Armenia","type":"country","country_code":"AM","supports_region":true,"supports_city":false},{"key":"AN","name":"Netherlands Antilles","type":"country","country_code":"AN","supports_region":false,"supports_city":false},{"key":"AO","name":"Angola","type":"country","country_code":"AO","supports_region":true,"supports_city":false},{"key":"AQ","name":"Antarctica","type":"country","country_code":"AQ","supports_region":false,"supports_city":false},{"key":"AR","name":"Argentina","type":"country","country_code":"AR","supports_region":true,"supports_city":true},{"key":"AS","name":"American Samoa","type":"country","country_code":"AS","supports_region":true,"supports_city":false},{"key":"AT","name":"Austria","type":"country","country_code":"AT","supports_region":true,"supports_city":true},{"key":"AU","name":"Australia","type":"country","country_code":"AU","supports_region":true,"supports_city":true},{"key":"AW","name":"Aruba","type":"country","country_code":"AW","supports_region":false,"supports_city":false},{"key":"AX","name":"Aland Islands","type":"country","country_code":"AX","supports_region":false,"supports_city":false},{"key":"AZ","name":"Azerbaijan","type":"country","country_code":"AZ","supports_region":true,"supports_city":false},{"key":"BA","name":"Bosnia and Herzegovina","type":"country","country_code":"BA","supports_region":false,"supports_city":false},{"key":"BB","name":"Barbados","type":"country","country_code":"BB","supports_region":true,"supports_city":false},{"key":"BD","name":"Bangladesh","type":"country","country_code":"BD","supports_region":true,"supports_city":false},{"key":"BE","name":"Belgium","type":"country","country_code":"BE","supports_region":true,"supports_city":true},{"key":"BF","name":"Burkina Faso","type":"country","country_code":"BF","supports_region":false,"supports_city":false},{"key":"BG","name":"Bulgaria","type":"country","country_code":"BG","supports_region":true,"supports_city":true},{"key":"BH","name":"Bahrain","type":"country","country_code":"BH","supports_region":true,"supports_city":false},{"key":"BI","name":"Burundi","type":"country","country_code":"BI","supports_region":true,"supports_city":false},{"key":"BJ","name":"Benin","type":"country","country_code":"BJ","supports_region":true,"supports_city":false},{"key":"BL","name":"Saint Barthélemy","type":"country","country_code":"BL","supports_region":false,"supports_city":false},{"key":"BM","name":"Bermuda","type":"country","country_code":"BM","supports_region":true,"supports_city":false},{"key":"BN","name":"Brunei","type":"country","country_code":"BN","supports_region":true,"supports_city":false},{"key":"BO","name":"Bolivia","type":"country","country_code":"BO","supports_region":true,"supports_city":true},{"key":"BQ","name":"Bonaire, Sint Eustatius and Saba","type":"country","country_code":"BQ","supports_region":false,"supports_city":false},{"key":"BR","name":"Brazil","type":"country","country_code":"BR","supports_region":true,"supports_city":true},{"key":"BS","name":"The Bahamas","type":"country","country_code":"BS","supports_region":false,"supports_city":false},{"key":"BT","name":"Bhutan","type":"country","country_code":"BT","supports_region":true,"supports_city":false},{"key":"BV","name":"Bouvet Island","type":"country","country_code":"BV","supports_region":false,"supports_city":false},{"key":"BW","name":"Botswana","type":"country","country_code":"BW","supports_region":true,"supports_city":false},{"key":"BY","name":"Belarus","type":"country","country_code":"BY","supports_region":true,"supports_city":false},{"key":"BZ","name":"Belize","type":"country","country_code":"BZ","supports_region":true,"supports_city":false},{"key":"CA","name":"Canada","type":"country","country_code":"CA","supports_region":true,"supports_city":true},{"key":"CC","name":"Cocos (Keeling) Islands","type":"country","country_code":"CC","supports_region":false,"supports_city":false},{"key":"CD","name":"Democratic Republic of the Congo","type":"country","country_code":"CD","supports_region":false,"supports_city":false},{"key":"CF","name":"Central African Republic","type":"country","country_code":"CF","supports_region":false,"supports_city":false},{"key":"CG","name":"Republic of the Congo","type":"country","country_code":"CG","supports_region":true,"supports_city":false},{"key":"CH","name":"Switzerland","type":"country","country_code":"CH","supports_region":true,"supports_city":true},{"key":"CI","name":"Côte d'Ivoire","type":"country","country_code":"CI","supports_region":true,"supports_city":false},{"key":"CK","name":"Cook Islands","type":"country","country_code":"CK","supports_region":true,"supports_city":false},{"key":"CL","name":"Chile","type":"country","country_code":"CL","supports_region":true,"supports_city":true},{"key":"CM","name":"Cameroon","type":"country","country_code":"CM","supports_region":true,"supports_city":false},{"key":"CN","name":"China","type":"country","country_code":"CN","supports_region":false,"supports_city":false},{"key":"CO","name":"Colombia","type":"country","country_code":"CO","supports_region":true,"supports_city":true},{"key":"CR","name":"Costa Rica","type":"country","country_code":"CR","supports_region":true,"supports_city":true},{"key":"CV","name":"Cape Verde","type":"country","country_code":"CV","supports_region":true,"supports_city":false},{"key":"CW","name":"Curaçao","type":"country","country_code":"CW","supports_region":false,"supports_city":false},{"key":"CX","name":"Christmas Island","type":"country","country_code":"CX","supports_region":false,"supports_city":false},{"key":"CY","name":"Cyprus","type":"country","country_code":"CY","supports_region":true,"supports_city":false},{"key":"CZ","name":"Czech Republic","type":"country","country_code":"CZ","supports_region":true,"supports_city":true},{"key":"DE","name":"Germany","type":"country","country_code":"DE","supports_region":true,"supports_city":true},{"key":"DJ","name":"Djibouti","type":"country","country_code":"DJ","supports_region":true,"supports_city":false},{"key":"DK","name":"Denmark","type":"country","country_code":"DK","supports_region":true,"supports_city":true},{"key":"DM","name":"Dominica","type":"country","country_code":"DM","supports_region":true,"supports_city":false},{"key":"DO","name":"Dominican Republic","type":"country","country_code":"DO","supports_region":true,"supports_city":true},{"key":"DZ","name":"Algeria","type":"country","country_code":"DZ","supports_region":true,"supports_city":false},{"key":"EC","name":"Ecuador","type":"country","country_code":"EC","supports_region":true,"supports_city":true},{"key":"EE","name":"Estonia","type":"country","country_code":"EE","supports_region":true,"supports_city":false},{"key":"EG","name":"Egypt","type":"country","country_code":"EG","supports_region":true,"supports_city":true},{"key":"EH","name":"Western Sahara","type":"country","country_code":"EH","supports_region":false,"supports_city":false},{"key":"ER","name":"Eritrea","type":"country","country_code":"ER","supports_region":true,"supports_city":false},{"key":"ES","name":"Spain","type":"country","country_code":"ES","supports_region":true,"supports_city":true},{"key":"ET","name":"Ethiopia","type":"country","country_code":"ET","supports_region":true,"supports_city":false},{"key":"FI","name":"Finland","type":"country","country_code":"FI","supports_region":true,"supports_city":true},{"key":"FJ","name":"Fiji","type":"country","country_code":"FJ","supports_region":true,"supports_city":false},{"key":"FK","name":"Falkland Islands","type":"country","country_code":"FK","supports_region":false,"supports_city":false},{"key":"FM","name":"Federated States of Micronesia","type":"country","country_code":"FM","supports_region":false,"supports_city":false},{"key":"FO","name":"Faroe Islands","type":"country","country_code":"FO","supports_region":true,"supports_city":false},{"key":"FR","name":"France","type":"country","country_code":"FR","supports_region":true,"supports_city":true},{"key":"GA","name":"Gabon","type":"country","country_code":"GA","supports_region":true,"supports_city":false},{"key":"GB","name":"United Kingdom","type":"country","country_code":"GB","supports_region":true,"supports_city":true},{"key":"GD","name":"Grenada","type":"country","country_code":"GD","supports_region":true,"supports_city":false},{"key":"GE","name":"Georgia","type":"country","country_code":"GE","supports_region":true,"supports_city":false},{"key":"GF","name":"French Guiana","type":"country","country_code":"GF","supports_region":false,"supports_city":false},{"key":"GG","name":"Guernsey","type":"country","country_code":"GG","supports_region":true,"supports_city":false},{"key":"GH","name":"Ghana","type":"country","country_code":"GH","supports_region":true,"supports_city":false},{"key":"GI","name":"Gibraltar","type":"country","country_code":"GI","supports_region":false,"supports_city":false},{"key":"GL","name":"Greenland","type":"country","country_code":"GL","supports_region":true,"supports_city":false},{"key":"GM","name":"The Gambia","type":"country","country_code":"GM","supports_region":true,"supports_city":false},{"key":"GN","name":"Guinea","type":"country","country_code":"GN","supports_region":true,"supports_city":false},{"key":"GP","name":"Guadeloupe","type":"country","country_code":"GP","supports_region":false,"supports_city":false},{"key":"GQ","name":"Equatorial Guinea","type":"country","country_code":"GQ","supports_region":true,"supports_city":false},{"key":"GR","name":"Greece","type":"country","country_code":"GR","supports_region":true,"supports_city":true},{"key":"GS","name":"South Georgia and the South Sandwich Islands","type":"country","country_code":"GS","supports_region":false,"supports_city":false},{"key":"GT","name":"Guatemala","type":"country","country_code":"GT","supports_region":true,"supports_city":true},{"key":"GU","name":"Guam","type":"country","country_code":"GU","supports_region":false,"supports_city":false},{"key":"GW","name":"Guinea-Bissau","type":"country","country_code":"GW","supports_region":true,"supports_city":false},{"key":"GY","name":"Guyana","type":"country","country_code":"GY","supports_region":true,"supports_city":false},{"key":"HK","name":"Hong Kong","type":"country","country_code":"HK","supports_region":true,"supports_city":false},{"key":"HM","name":"Heard Island and McDonald Islands","type":"country","country_code":"HM","supports_region":false,"supports_city":false},{"key":"HN","name":"Honduras","type":"country","country_code":"HN","supports_region":true,"supports_city":true},{"key":"HR","name":"Croatia","type":"country","country_code":"HR","supports_region":true,"supports_city":false},{"key":"HT","name":"Haiti","type":"country","country_code":"HT","supports_region":true,"supports_city":false},{"key":"HU","name":"Hungary","type":"country","country_code":"HU","supports_region":true,"supports_city":true},{"key":"ID","name":"Indonesia","type":"country","country_code":"ID","supports_region":true,"supports_city":true},{"key":"IE","name":"Ireland","type":"country","country_code":"IE","supports_region":true,"supports_city":true},{"key":"IL","name":"Israel","type":"country","country_code":"IL","supports_region":true,"supports_city":true},{"key":"IM","name":"Isle Of Man","type":"country","country_code":"IM","supports_region":false,"supports_city":false},{"key":"IN","name":"India","type":"country","country_code":"IN","supports_region":true,"supports_city":true},{"key":"IO","name":"British Indian Ocean Territory","type":"country","country_code":"IO","supports_region":false,"supports_city":false},{"key":"IQ","name":"Iraq","type":"country","country_code":"IQ","supports_region":true,"supports_city":false},{"key":"IS","name":"Iceland","type":"country","country_code":"IS","supports_region":true,"supports_city":false},{"key":"IT","name":"Italy","type":"country","country_code":"IT","supports_region":true,"supports_city":true},{"key":"JE","name":"Jersey","type":"country","country_code":"JE","supports_region":true,"supports_city":false},{"key":"JM","name":"Jamaica","type":"country","country_code":"JM","supports_region":true,"supports_city":false},{"key":"JO","name":"Jordan","type":"country","country_code":"JO","supports_region":true,"supports_city":false},{"key":"JP","name":"Japan","type":"country","country_code":"JP","supports_region":true,"supports_city":true},{"key":"KE","name":"Kenya","type":"country","country_code":"KE","supports_region":true,"supports_city":false},{"key":"KG","name":"Kyrgyzstan","type":"country","country_code":"KG","supports_region":true,"supports_city":false},{"key":"KH","name":"Cambodia","type":"country","country_code":"KH","supports_region":true,"supports_city":false},{"key":"KI","name":"Kiribati","type":"country","country_code":"KI","supports_region":false,"supports_city":false},{"key":"KM","name":"Comoros","type":"country","country_code":"KM","supports_region":true,"supports_city":false},{"key":"KN","name":"Saint Kitts and Nevis","type":"country","country_code":"KN","supports_region":false,"supports_city":false},{"key":"KP","name":"North Korea","type":"country","country_code":"KP","supports_region":false,"supports_city":false},{"key":"KR","name":"South Korea","type":"country","country_code":"KR","supports_region":true,"supports_city":true},{"key":"KW","name":"Kuwait","type":"country","country_code":"KW","supports_region":true,"supports_city":false},{"key":"KY","name":"Cayman Islands","type":"country","country_code":"KY","supports_region":true,"supports_city":false},{"key":"KZ","name":"Kazakhstan","type":"country","country_code":"KZ","supports_region":true,"supports_city":false},{"key":"LA","name":"Laos","type":"country","country_code":"LA","supports_region":true,"supports_city":false},{"key":"LB","name":"Lebanon","type":"country","country_code":"LB","supports_region":true,"supports_city":false},{"key":"LC","name":"St. Lucia","type":"country","country_code":"LC","supports_region":false,"supports_city":false},{"key":"LI","name":"Liechtenstein","type":"country","country_code":"LI","supports_region":true,"supports_city":false},{"key":"LK","name":"Sri Lanka","type":"country","country_code":"LK","supports_region":true,"supports_city":false},{"key":"LR","name":"Liberia","type":"country","country_code":"LR","supports_region":true,"supports_city":false},{"key":"LS","name":"Lesotho","type":"country","country_code":"LS","supports_region":true,"supports_city":false},{"key":"LT","name":"Lithuania","type":"country","country_code":"LT","supports_region":true,"supports_city":false},{"key":"LU","name":"Luxembourg","type":"country","country_code":"LU","supports_region":true,"supports_city":false},{"key":"LV","name":"Latvia","type":"country","country_code":"LV","supports_region":true,"supports_city":false},{"key":"LY","name":"Libya","type":"country","country_code":"LY","supports_region":true,"supports_city":false},{"key":"MA","name":"Morocco","type":"country","country_code":"MA","supports_region":true,"supports_city":false},{"key":"MC","name":"Monaco","type":"country","country_code":"MC","supports_region":true,"supports_city":false},{"key":"MD","name":"Moldova","type":"country","country_code":"MD","supports_region":true,"supports_city":false},{"key":"ME","name":"Montenegro","type":"country","country_code":"ME","supports_region":true,"supports_city":false},{"key":"MF","name":"Saint Martin","type":"country","country_code":"MF","supports_region":false,"supports_city":false},{"key":"MG","name":"Madagascar","type":"country","country_code":"MG","supports_region":true,"supports_city":false},{"key":"MH","name":"Marshall Islands","type":"country","country_code":"MH","supports_region":true,"supports_city":false},{"key":"MK","name":"Macedonia","type":"country","country_code":"MK","supports_region":true,"supports_city":false},{"key":"ML","name":"Mali","type":"country","country_code":"ML","supports_region":true,"supports_city":false},{"key":"MM","name":"Myanmar","type":"country","country_code":"MM","supports_region":false,"supports_city":false},{"key":"MN","name":"Mongolia","type":"country","country_code":"MN","supports_region":true,"supports_city":false},{"key":"MO","name":"Macau","type":"country","country_code":"MO","supports_region":false,"supports_city":false},{"key":"MP","name":"Northern Mariana Islands","type":"country","country_code":"MP","supports_region":false,"supports_city":false},{"key":"MQ","name":"Martinique","type":"country","country_code":"MQ","supports_region":false,"supports_city":false},{"key":"MR","name":"Mauritania","type":"country","country_code":"MR","supports_region":true,"supports_city":false},{"key":"MS","name":"Montserrat","type":"country","country_code":"MS","supports_region":false,"supports_city":false},{"key":"MT","name":"Malta","type":"country","country_code":"MT","supports_region":true,"supports_city":false},{"key":"MU","name":"Mauritius","type":"country","country_code":"MU","supports_region":true,"supports_city":false},{"key":"MV","name":"Maldives","type":"country","country_code":"MV","supports_region":true,"supports_city":false},{"key":"MW","name":"Malawi","type":"country","country_code":"MW","supports_region":true,"supports_city":false},{"key":"MX","name":"Mexico","type":"country","country_code":"MX","supports_region":true,"supports_city":true},{"key":"MY","name":"Malaysia","type":"country","country_code":"MY","supports_region":true,"supports_city":true},{"key":"MZ","name":"Mozambique","type":"country","country_code":"MZ","supports_region":true,"supports_city":false},{"key":"NA","name":"Namibia","type":"country","country_code":"NA","supports_region":true,"supports_city":false},{"key":"NC","name":"New Caledonia","type":"country","country_code":"NC","supports_region":true,"supports_city":false},{"key":"NE","name":"Niger","type":"country","country_code":"NE","supports_region":true,"supports_city":false},{"key":"NF","name":"Norfolk Island","type":"country","country_code":"NF","supports_region":false,"supports_city":false},{"key":"NG","name":"Nigeria","type":"country","country_code":"NG","supports_region":true,"supports_city":true},{"key":"NI","name":"Nicaragua","type":"country","country_code":"NI","supports_region":true,"supports_city":true},{"key":"NL","name":"Netherlands","type":"country","country_code":"NL","supports_region":true,"supports_city":true},{"key":"NO","name":"Norway","type":"country","country_code":"NO","supports_region":true,"supports_city":true},{"key":"NP","name":"Nepal","type":"country","country_code":"NP","supports_region":true,"supports_city":false},{"key":"NR","name":"Nauru","type":"country","country_code":"NR","supports_region":true,"supports_city":false},{"key":"NU","name":"Niue","type":"country","country_code":"NU","supports_region":false,"supports_city":false},{"key":"NZ","name":"New Zealand","type":"country","country_code":"NZ","supports_region":true,"supports_city":true},{"key":"OM","name":"Oman","type":"country","country_code":"OM","supports_region":true,"supports_city":false},{"key":"PA","name":"Panama","type":"country","country_code":"PA","supports_region":true,"supports_city":true},{"key":"PE","name":"Peru","type":"country","country_code":"PE","supports_region":true,"supports_city":true},{"key":"PF","name":"French Polynesia","type":"country","country_code":"PF","supports_region":true,"supports_city":false},{"key":"PG","name":"Papua New Guinea","type":"country","country_code":"PG","supports_region":true,"supports_city":false},{"key":"PH","name":"Philippines","type":"country","country_code":"PH","supports_region":true,"supports_city":true},{"key":"PK","name":"Pakistan","type":"country","country_code":"PK","supports_region":true,"supports_city":false},{"key":"PL","name":"Poland","type":"country","country_code":"PL","supports_region":true,"supports_city":true},{"key":"PM","name":"Saint Pierre and Miquelon","type":"country","country_code":"PM","supports_region":false,"supports_city":false},{"key":"PN","name":"Pitcairn","type":"country","country_code":"PN","supports_region":false,"supports_city":false},{"key":"PR","name":"Puerto Rico","type":"country","country_code":"PR","supports_region":true,"supports_city":true},{"key":"PS","name":"Palestine","type":"country","country_code":"PS","supports_region":false,"supports_city":false},{"key":"PT","name":"Portugal","type":"country","country_code":"PT","supports_region":true,"supports_city":true},{"key":"PW","name":"Palau","type":"country","country_code":"PW","supports_region":false,"supports_city":false},{"key":"PY","name":"Paraguay","type":"country","country_code":"PY","supports_region":true,"supports_city":true},{"key":"QA","name":"Qatar","type":"country","country_code":"QA","supports_region":true,"supports_city":false},{"key":"RE","name":"Réunion","type":"country","country_code":"RE","supports_region":false,"supports_city":false},{"key":"RO","name":"Romania","type":"country","country_code":"RO","supports_region":true,"supports_city":true},{"key":"RS","name":"Serbia","type":"country","country_code":"RS","supports_region":false,"supports_city":true},{"key":"RU","name":"Russia","type":"country","country_code":"RU","supports_region":true,"supports_city":true},{"key":"RW","name":"Rwanda","type":"country","country_code":"RW","supports_region":true,"supports_city":false},{"key":"SA","name":"Saudi Arabia","type":"country","country_code":"SA","supports_region":true,"supports_city":true},{"key":"SB","name":"Solomon Islands","type":"country","country_code":"SB","supports_region":true,"supports_city":false},{"key":"SC","name":"Seychelles","type":"country","country_code":"SC","supports_region":true,"supports_city":false},{"key":"SE","name":"Sweden","type":"country","country_code":"SE","supports_region":true,"supports_city":true},{"key":"SG","name":"Singapore","type":"country","country_code":"SG","supports_region":true,"supports_city":false},{"key":"SH","name":"Saint Helena","type":"country","country_code":"SH","supports_region":true,"supports_city":false},{"key":"SI","name":"Slovenia","type":"country","country_code":"SI","supports_region":true,"supports_city":false},{"key":"SJ","name":"Svalbard and Jan Mayen","type":"country","country_code":"SJ","supports_region":false,"supports_city":false},{"key":"SK","name":"Slovakia","type":"country","country_code":"SK","supports_region":true,"supports_city":false},{"key":"SL","name":"Sierra Leone","type":"country","country_code":"SL","supports_region":true,"supports_city":false},{"key":"SM","name":"San Marino","type":"country","country_code":"SM","supports_region":true,"supports_city":false},{"key":"SN","name":"Senegal","type":"country","country_code":"SN","supports_region":true,"supports_city":false},{"key":"SO","name":"Somalia","type":"country","country_code":"SO","supports_region":true,"supports_city":false},{"key":"SR","name":"Suriname","type":"country","country_code":"SR","supports_region":true,"supports_city":false},{"key":"SS","name":"South Sudan","type":"country","country_code":"SS","supports_region":true,"supports_city":false},{"key":"ST","name":"Sao Tome and Principe","type":"country","country_code":"ST","supports_region":true,"supports_city":false},{"key":"SV","name":"El Salvador","type":"country","country_code":"SV","supports_region":true,"supports_city":true},{"key":"SX","name":"Sint Maarten","type":"country","country_code":"SX","supports_region":false,"supports_city":false},{"key":"SZ","name":"Swaziland","type":"country","country_code":"SZ","supports_region":true,"supports_city":false},{"key":"TC","name":"Turks and Caicos Islands","type":"country","country_code":"TC","supports_region":false,"supports_city":false},{"key":"TD","name":"Chad","type":"country","country_code":"TD","supports_region":true,"supports_city":false},{"key":"TF","name":"French Southern Territories","type":"country","country_code":"TF","supports_region":false,"supports_city":false},{"key":"TG","name":"Togo","type":"country","country_code":"TG","supports_region":true,"supports_city":false},{"key":"TH","name":"Thailand","type":"country","country_code":"TH","supports_region":true,"supports_city":true},{"key":"TJ","name":"Tajikistan","type":"country","country_code":"TJ","supports_region":true,"supports_city":false},{"key":"TK","name":"Tokelau","type":"country","country_code":"TK","supports_region":true,"supports_city":false},{"key":"TL","name":"Timor-Leste","type":"country","country_code":"TL","supports_region":false,"supports_city":false},{"key":"TM","name":"Turkmenistan","type":"country","country_code":"TM","supports_region":true,"supports_city":false},{"key":"TN","name":"Tunisia","type":"country","country_code":"TN","supports_region":true,"supports_city":false},{"key":"TO","name":"Tonga","type":"country","country_code":"TO","supports_region":true,"supports_city":false},{"key":"TR","name":"Turkey","type":"country","country_code":"TR","supports_region":true,"supports_city":true},{"key":"TT","name":"Trinidad and Tobago","type":"country","country_code":"TT","supports_region":true,"supports_city":false},{"key":"TV","name":"Tuvalu","type":"country","country_code":"TV","supports_region":true,"supports_city":false},{"key":"TW","name":"Taiwan","type":"country","country_code":"TW","supports_region":true,"supports_city":true},{"key":"TZ","name":"Tanzania","type":"country","country_code":"TZ","supports_region":false,"supports_city":false},{"key":"UA","name":"Ukraine","type":"country","country_code":"UA","supports_region":true,"supports_city":true},{"key":"UG","name":"Uganda","type":"country","country_code":"UG","supports_region":true,"supports_city":false},{"key":"UM","name":"United States Minor Outlying Islands","type":"country","country_code":"UM","supports_region":false,"supports_city":false},{"key":"US","name":"United States","type":"country","country_code":"US","supports_region":true,"supports_city":true},{"key":"UY","name":"Uruguay","type":"country","country_code":"UY","supports_region":true,"supports_city":true},{"key":"UZ","name":"Uzbekistan","type":"country","country_code":"UZ","supports_region":true,"supports_city":false},{"key":"VA","name":"Vatican City","type":"country","country_code":"VA","supports_region":false,"supports_city":false},{"key":"VC","name":"Saint Vincent and the Grenadines","type":"country","country_code":"VC","supports_region":false,"supports_city":false},{"key":"VE","name":"Venezuela","type":"country","country_code":"VE","supports_region":true,"supports_city":true},{"key":"VG","name":"British Virgin Islands","type":"country","country_code":"VG","supports_region":false,"supports_city":false},{"key":"VI","name":"US Virgin Islands","type":"country","country_code":"VI","supports_region":false,"supports_city":false},{"key":"VN","name":"Vietnam","type":"country","country_code":"VN","supports_region":true,"supports_city":true},{"key":"VU","name":"Vanuatu","type":"country","country_code":"VU","supports_region":true,"supports_city":false},{"key":"WF","name":"Wallis and Futuna","type":"country","country_code":"WF","supports_region":false,"supports_city":false},{"key":"WS","name":"Samoa","type":"country","country_code":"WS","supports_region":false,"supports_city":false},{"key":"XK","name":"Kosovo","type":"country","country_code":"XK","supports_region":false,"supports_city":false},{"key":"YE","name":"Yemen","type":"country","country_code":"YE","supports_region":true,"supports_city":false},{"key":"YT","name":"Mayotte","type":"country","country_code":"YT","supports_region":false,"supports_city":false},{"key":"ZA","name":"South Africa","type":"country","country_code":"ZA","supports_region":true,"supports_city":true},{"key":"ZM","name":"Zambia","type":"country","country_code":"ZM","supports_region":true,"supports_city":false},{"key":"ZW","name":"Zimbabwe","type":"country","country_code":"ZW","supports_region":true,"supports_city":false}];
var languageList = ["", "Afrikaans", "Albanian", "Arabic", "Armenian", "Azeerbaijani", "Basque", "Belarusian", "Bengali", "Bosnian", "Bulgarian", "Catalan", "Czech", "Cebuano", "Chinese (All)", "Croatian", "Danish", "Dutch", "Dutch (België)", "English (US)", "English (All)", "English (UK)", "Esperanto", "Estonian", "Filipino", "Faroese", "Finnish", "French (All)", "French (Canada)", "French (France)", "Frisian", "Galician", "German", "Georgian", "Greek", "Gujarati", "Guarani", "Hungarian", "Hindi", "Hebrew", "Icelandic", "Indonesian", "Irish", "Italian", "Japanese", "Japanese (Kansai)", "Javanese", "Kannada", "Kazakh", "Khmer", "Korean", "Kurdish (Kurmanji)", "Leet Speak", "Latin", "Latvian", "Lithuanian", "Macedonian", "Malayalam", "Malay", "Marathi", "Mongolian", "Nepali", "Norwegian (bokmal)", "Norwegian (nynorsk)", "Punjabi", "Polish", "Portuguese (All)", "Portuguese (Brazil)", "Portuguese (Portugal)", "Pashto", "Persian", "Romanian", "Russian", "Spanish", "Spanish (Spain)", "Serbian", "Swahili", "Slovak", "Slovenian", "Sinhala", "Spanish (All)", "Simplified Chinese (China)", "Swedish", "Thai", "Turkish", "Traditional Chinese (Hong Kong)", "Traditional Chinese (Taiwan)", "Tamil", "Tajik", "Telugu", "Urdu", "Ukrainian", "Vietnamese", "Welsh"];
var admobLanguageCodes = [{"name":"All","code":""},{"name":"Arabic","code":1019},{"name":"Bulgarian","code":1020},{"name":"Catalan","code":1038},{"name":"Chinese (simplified)","code":1017},{"name":"Chinese (traditional)","code":1018},{"name":"Croatian","code":1039},{"name":"Czech","code":1021},{"name":"Danish","code":1009},{"name":"Dutch","code":1010},{"name":"English","code":1000},{"name":"Estonian","code":1043},{"name":"Filipino","code":1042},{"name":"Finnish","code":1011},{"name":"French","code":1002},{"name":"German","code":1001},{"name":"Greek","code":1022},{"name":"Hebrew","code":1027},{"name":"Hindi","code":1023},{"name":"Hungarian","code":1024},{"name":"Icelandic","code":1026},{"name":"Indonesian","code":1025},{"name":"Italian","code":1004},{"name":"Japanese","code":1005},{"name":"Korean","code":1012},{"name":"Latvian","code":1028},{"name":"Lithuanian","code":1029},{"name":"Malay","code":1102},{"name":"Norwegian","code":1013},{"name":"Persian","code":1064},{"name":"Polish","code":1030},{"name":"Portuguese","code":1014},{"name":"Romanian","code":1032},{"name":"Russian","code":1031},{"name":"Serbian","code":1035},{"name":"Slovak","code":1033},{"name":"Slovenian","code":1034},{"name":"Spanish","code":1003},{"name":"Swedish","code":1015},{"name":"Thai","code":1044},{"name":"Turkish","code":1037},{"name":"Ukrainian","code":1036},{"name":"Urdu","code":1041},{"name":"Vietnamese","code":1040}];
var genderList = ["", "男", "女"];
var osList = ["","Android_ver_4.0_and_above","Android_ver_5.0_and_above","Android_ver_6.0_and_above","Android_ver_7.0_and_above","Android_ver_8.0_and_above","Android_ver_4.0_to_5.0","Android_ver_5.0_to_6.0","Android_ver_6.0_to_7.0","Android_ver_7.0_to_8.0"];
var appList = [];
var admobRegionCodes = {"All":"","Sint Maarten":"SX","Cuba":"CU","Curacao":"CW","Iran":"IR","Afghanistan":"AF","Albania":"AL","Antarctica":"AQ","Algeria":"DZ","American Samoa":"AS","Andorra":"AD","Angola":"AO","Antigua and Barbuda":"AG","Azerbaijan":"AZ","Argentina":"AR","Australia":"AU","Austria":"AT","The Bahamas":"BS","Bahrain":"BH","Bangladesh":"BD","Armenia":"AM","Barbados":"BB","Belgium":"BE","Bermuda":"BM","Bhutan":"BT","Bolivia":"BO","Bosnia and Herzegovina":"BA","Botswana":"BW","Bouvet Island":"BV","Brazil":"BR","Belize":"BZ","British Indian Ocean Territory":"IO","Solomon Islands":"SB","British Virgin Islands":"VG","Brunei":"BN","Bulgaria":"BG","Myanmar (Burma)":"MM","Burundi":"BI","Belarus":"BY","Cambodia":"KH","Cameroon":"CM","Canada":"CA","Cape Verde":"CV","Cayman Islands":"KY","Central African Republic":"CF","Sri Lanka":"LK","Chad":"TD","Chile":"CL","China":"CN","Taiwan":"TW","Christmas Island":"CX","Cocos (Keeling) Islands":"CC","Colombia":"CO","Comoros":"KM","Mayotte":"YT","Republic of the Congo":"CG","Democratic Republic of the Congo":"CD","Cook Islands":"CK","Costa Rica":"CR","Croatia":"HR","Cyprus":"CY","Czechia":"CZ","Benin":"BJ","Denmark":"DK","Dominica":"DM","Dominican Republic":"DO","Ecuador":"EC","El Salvador":"SV","Equatorial Guinea":"GQ","Ethiopia":"ET","Eritrea":"ER","Estonia":"EE","Faroe Islands":"FO","Falkland Islands (Islas Malvinas)":"FK","South Georgia and the South Sandwich Islands":"GS","Fiji":"FJ","Finland":"FI","France":"FR","French Guiana":"GF","French Polynesia":"PF","French Southern and Antarctic Lands":"TF","Djibouti":"DJ","Gabon":"GA","Georgia":"GE","The Gambia":"GM","Palestine":"PS","Germany":"DE","Ghana":"GH","Gibraltar":"GI","Kiribati":"KI","Greece":"GR","Greenland":"GL","Grenada":"GD","Guadeloupe":"GP","Guam":"GU","Guatemala":"GT","Guinea":"GN","Guyana":"GY","Haiti":"HT","Heard Island and McDonald Islands":"HM","Vatican City":"VA","Honduras":"HN","Hong Kong":"HK","Hungary":"HU","Iceland":"IS","India":"IN","Indonesia":"ID","Iraq":"IQ","Ireland":"IE","Israel":"IL","Italy":"IT","Cote d'Ivoire":"CI","Jamaica":"JM","Japan":"JP","Kazakhstan":"KZ","Jordan":"JO","Kenya":"KE","South Korea":"KR","Kuwait":"KW","Kyrgyzstan":"KG","Laos":"LA","Lebanon":"LB","Lesotho":"LS","Latvia":"LV","Liberia":"LR","Libya":"LY","Liechtenstein":"LI","Lithuania":"LT","Luxembourg":"LU","Macau":"MO","Madagascar":"MG","Malawi":"MW","Malaysia":"MY","Maldives":"MV","Mali":"ML","Malta":"MT","Martinique":"MQ","Mauritania":"MR","Mauritius":"MU","Mexico":"MX","Monaco":"MC","Mongolia":"MN","Moldova":"MD","Montenegro":"ME","Montserrat":"MS","Morocco":"MA","Mozambique":"MZ","Oman":"OM","Namibia":"NA","Nauru":"NR","Nepal":"NP","Netherlands":"NL","Netherlands Antilles":"BQ","Aruba":"AW","New Caledonia":"NC","Vanuatu":"VU","New Zealand":"NZ","Nicaragua":"NI","Niger":"NE","Nigeria":"NG","Niue":"NU","Norfolk Island":"NF","Norway":"NO","Northern Mariana Islands":"MP","United States Minor Outlying Islands":"UM","Federated States of Micronesia":"FM","Marshall Islands":"MH","Palau":"PW","Pakistan":"PK","Panama":"PA","Papua New Guinea":"PG","Paraguay":"PY","Peru":"PE","Philippines":"PH","Pitcairn Islands":"PN","Poland":"PL","Portugal":"PT","Guinea-Bissau":"GW","Timor-Leste":"TL","Puerto Rico":"PR","Qatar":"QA","Reunion":"RE","Romania":"RO","Russia":"RU","Rwanda":"RW","Saint Helena, Ascension and Tristan da Cunha":"SH","Saint Kitts and Nevis":"KN","Anguilla":"AI","Saint Lucia":"LC","Saint Pierre and Miquelon":"PM","Saint Vincent and the Grenadines":"VC","San Marino":"SM","Sao Tome and Principe":"ST","Saudi Arabia":"SA","Senegal":"SN","Serbia":"RS","Seychelles":"SC","Sierra Leone":"SL","Singapore":"SG","Slovakia":"SK","Vietnam":"VN","Slovenia":"SI","Somalia":"SO","South Africa":"ZA","Zimbabwe":"ZW","Spain":"ES","Western Sahara":"EH","Suriname":"SR","Svalbard and Jan Mayen":"SJ","Swaziland":"SZ","Sweden":"SE","Switzerland":"CH","Tajikistan":"TJ","Thailand":"TH","Togo":"TG","Tokelau":"TK","Tonga":"TO","Trinidad and Tobago":"TT","United Arab Emirates":"AE","Tunisia":"TN","Turkey":"TR","Turkmenistan":"TM","Turks and Caicos Islands":"TC","Tuvalu":"TV","Uganda":"UG","Ukraine":"UA","Macedonia (FYROM)":"MK","Egypt":"EG","United Kingdom":"GB","Guernsey":"GG","Jersey":"JE","Tanzania":"TZ","United States":"US","U.S. Virgin Islands":"VI","Burkina Faso":"BF","Uruguay":"UY","Uzbekistan":"UZ","Venezuela":"VE","Wallis and Futuna":"WF","Samoa":"WS","Yemen":"YE","Zambia":"ZM","Kosovo":"XK"};

//一种可多选的autocomplete方法封装
function multiSelectAutocomplete(selector,valueList){
    function split( val ) {
        return val.split( /,\s*/ );  //逗号及其后跟着的字符串作为分割标志
    }
    function extractLast( term ) {
        return split( term ).pop();  //分割后的数组返回并删除数组的最后一个元素
    }
    $( "#"+ selector )
    // 当选择一个条目时不离开文本域
        .bind( "keydown", function( event ) {
            if ( event.keyCode === $.ui.keyCode.TAB &&
                $( this ).data( "ui-autocomplete" ).menu.active ) {
                event.preventDefault();  //阻止event对象的默认行为发生
            }
        })
        .autocomplete({
            minLength: 0,
            source: function( request, response ) {
                // 回到 autocomplete，但是提取最后的条目
                response($.ui.autocomplete.filter(
                    valueList, extractLast(request.term)));
            },
            focus: function () {
                // 防止在获得焦点时插入值
                return false;
            },
            select: function (event, ui) {
                var terms = split(this.value);
                // 移除当前输入
                terms.pop();
                // 添加被选项
                terms.push(ui.item.value);
                // 添加占位符，在结尾添加逗号
                terms.push("");
                this.value = terms.join(",");
                return false;
            }
        });
}

function init() {
    $('.select2').select2();

    $('#btnCampaignStatus').click(function () {
        popupCenter("campaign_status.jsp", "创建状态监控", 600, 480);
    });

    //“批量输入”: <input type="button">
    $('.btn-more').click(function () {
        var id = $(this).attr('id');
        var targetId = '';
        if (id == 'btnSelectRegionMore') {
            targetId = '#selectRegion';
        } else if (id == 'btnSelectRegionUnselectedMore') {
            targetId = '#selectRegionUnselected';
        } else if (id == 'btnSelectRegionUnselectedAdmobMore') {
            targetId = '#selectRegionUnselectedAdmob';
        } else if (id == 'btnSelectRegionAdmobMore') {
            targetId = '#selectRegionAdmob';
        }
        $('#moreCountryDlg').modal("show");   //modal() 是一个Bootstrap模态框插件
        $('#moreCountryDlg .btn-primary').off('click');
        $('#moreCountryDlg .btn-primary').click(function () {
            console.log(id);
            var countryAlias = $('#inputCountryAlias').val();
            var data = $('#textareaCountry').val();
            var countryList = data.split('\n');
            var countryNames = [];
            countryList.forEach(function (one) {
                for (var i = 0; i < regionList.length; i++) {
                    one = one.trim();
                    if (regionList[i].name.toLocaleLowerCase() == one.toLocaleLowerCase()) {
                        if (targetId == '#selectRegionUnselectedAdmob' || targetId == '#selectRegionAdmob') {
                            countryNames.push(regionList[i].country_code);
                        } else {
                            countryNames.push(regionList[i].name);
                        }
                        break;
                    }
                }
            });

            if (targetId != '') {
                if (countryNames.length > 0) {
                    $(targetId)[0].countryAlisa = countryAlias;
                } else {
                    $(targetId)[0].countryAlisa = null;
                }
                $(targetId).val(countryNames);
                $(targetId).trigger('change');
            }
            $('#moreCountryDlg').modal("hide");
        });
    });

    //以下的六个遍历用于动态添加选项
    languageList.forEach(function (one) {
        $('#selectLanguage').append($("<option>" + one + "</option>"));
    });
    $("#selectLanguage option:first-child").prop("selected",true);
    genderList.forEach(function (one) {
        $('#selectGender').append($("<option>" + one + "</option>"));
    });
    $("#selectGender option:first-child").prop("selected",true);
    osList.forEach(function (one) {
        $('#selectUserOs').append($("<option>" + one + "</option>"));
    });
    $('#selectUserOs option:first-child').prop("selected",true);
    regionList.forEach(function (one) {
        $('#selectRegion').append($("<option>" + one.name + "</option>"));
        $('#selectRegionUnselected').append($("<option>" + one.name + "</option>"));
    });
    $("#selectRegionUnselected").append("<option selected></option>");
    admobLanguageCodes.forEach(function (one) {
        $('#selectLanguageAdmob').append($("<option value='" + one.code + "'>" + one.name + "</option>"));
    });

    for (var k in admobRegionCodes) {
        var key, value;
        key = k;
        value = admobRegionCodes[k];
        $('#selectRegionAdmob').append($("<option value='" + value + "'>" + key + "</option>"));
        $('#selectRegionUnselectedAdmob').append($("<option value='" + value + "'>" + key + "</option>"));
    }
    $('#selectRegionUnselectedAdmob').append("<option selected></option>");
    var pendingList = [1, 2, 3];
    /*
     * 三个 $.post 是异步执行的，哪个先返回response就先执行哪一个的 function
     * 数组 pendingList 的作用在于当最后一个response 返回后执行 initFormData()
     * 而initFormData()是需要三个参数 isAutoCreate, modifyNetwork,modifyRecordId
     */
    $.post('system/fb_app_id_rel/query', {
        word: '',
    }, function (data) {
        if (data && data.ret == 1) {
            appList = data.data;
            appList.forEach(function (one) {
                $('#selectApp').append($("<option>" + one.tag_name + "</option>"));
                $('#selectAppAdmob').append($("<option>" + one.tag_name + "</option>"));
            });
            $("#inputImagePath").val(appList[0].tag_name + "/");
            $("#inputImagePathAdmob").val(appList[0].tag_name + "/");
            pendingList.shift();
            if (pendingList.length == 0) {
                if(isAutoCreate && modifyNetwork!=null && modifyRecordId!=null){
                    initFormData();
                }
            }
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');

    $.post('adaccount_admob/query', {word: ''}, function (data) {
        if (data && data.ret == 1) {
            var accountList = data.data;
            accountList.forEach(function (one) {
                $('#selectAccountAdmob').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
            });
            pendingList.shift();
            if (pendingList.length == 0) {
                if(isAutoCreate && modifyNetwork!=null && modifyRecordId!=null){
                    initFormData();
                }
            }
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');

    $.post('adaccount/query', {word: ''}, function (data) {
        if (data && data.ret == 1) {
            var accountList = data.data;
            accountList.forEach(function (one) {
                $('#selectAccount').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
            });
            pendingList.shift();
            if (pendingList.length == 0) {
                if(isAutoCreate && modifyNetwork!=null && modifyRecordId!=null){
                    initFormData();
                }
            }
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');

    $('#formAdmob').hide();

}

//执行初始化的方法
init();

/*
 * 该方法用于campaigns_auto_create.jsp与country_analysis_report.jsp页面传来 isAutoCreate 参数时执行
 * 在isAutoCreate情况下，需要 modifyNetwork以及 modifyRecordId
 * 这三个参数是由 campaigns_auto_create.jsp页面传来，用于自动填写选项
 * 此方法仅在campaign_create.js 的 function init(){}内的三个post中调用
 */
function initFormData() {
    if (isAutoCreate) {
        $.post('auto_create_campaign/' + modifyNetwork + '/query_by_id', {
            id: modifyRecordId
        }, function (data) {
            if (data && data.ret == 1) {
                console.log(data);
                if (modifyNetwork == 'facebook') {
                    $('#checkFacebook').prop('checked', true);
                    $('#checkFacebook').click();

                    var campaignData = data.data;
                    var accountIds = campaignData.account_id.split(",");
                    $('#selectApp').val(campaignData.app_name);
                    $('#selectAccount').val(accountIds);
                    $('#selectAccount').trigger('change');
                    $("#inputCreateCount").val(campaignData.create_count);
                    $('#selectRegion').val(campaignData.country_region.split(','));
                    $('#selectRegion').trigger('change');
                    $('#selectRegionExplode').prop('checked', campaignData.explode_country == 1);
                    $('#selectRegionUnselected').val(campaignData.excluded_region.split(','));
                    $('#selectRegionUnselected').trigger('change');
                    $('#selectLanguage').val(campaignData.language);
                    $('#inputAge').val(campaignData.age);
                    $('#inputAgeExplode').prop('checked', campaignData.explode_age == 1);
                    $('#selectGender').val(campaignData.gender.split(','));
                    $('#selectGender').trigger('change');
                    $('#selectGenderExplode').prop('checked', campaignData.explode_gender == 1);
                    $('#inputInterest').val(campaignData.detail_target);
                    $('#selectUserOs').val(campaignData.user_os.split(','));
                    $('#selectUserOs').trigger('change');
                    $('#inputUserDevices').val(campaignData.user_devices);
                    $('#inputBudget').val(campaignData.bugdet);
                    $('#inputBidding').val(campaignData.bidding);
                    $('#inputBiddingExplode').prop('checked', campaignData.explode_bidding == 1);
                    $('#inputMaxCpa').val(campaignData.max_cpa);
                    $('#inputTitle').val(campaignData.title);
                    $('#inputMessage').val(campaignData.message);
                    var imageTrimed = campaignData.image_path.replace(/home\/\w+\/\w+\/\w+\//,"");
                    $('#inputImagePath').val(imageTrimed);
                    $('#inputCampaignName').val(campaignData.campaign_name);

                    $('#btnCreate').val('更新');
                    $(".form-check-input").prop("checked",false);
                    $(".form-check-input").prop("disabled",true);
                    $("#onlyCheckAutoCreate").prop("checked",true);
                    $("#onlyCheckAutoCreate").prop("disabled",true);
                    $("#checkAutoCreate").prop("disabled",true);
                } else {
                    $('#checkAdmob').prop('checked', true);
                    $('#checkAdmob').click();

                    var campaignData = data.data;
                    var accountIds = campaignData.account_id.split(",");
                    $('#selectAppAdmob').val(campaignData.app_name);
                    $('#selectAccountAdmob').val(accountIds);
                    $('#selectAccountAdmob').trigger('change');
                    $("#inputCreateCountAdmob").val(campaignData.create_count);
                    $('#selectRegionAdmob').val(campaignData.country_region.split(','));
                    $('#selectRegionAdmob').trigger('change');
                    $('#selectRegionAdmobExplode').prop('checked', campaignData.explode_country == 1);
                    $('#selectRegionUnselectedAdmob').val(campaignData.excluded_region.split(','));
                    $('#selectRegionUnselectedAdmob').trigger('change');
                    $('#selectLanguageAdmob').val(campaignData.language);
                    $('#inputBudgetAdmob').val(campaignData.bugdet);
                    $('#inputBiddingAdmob').val(campaignData.bidding);
                    $('#inputBiddingAdmobExplode').prop('checked', campaignData.explode_bidding == 1);
                    $('#inputMaxCpaAdmob').val(campaignData.max_cpa);
                    $('#inputMessage1').val(campaignData.message1);
                    $('#inputMessage2').val(campaignData.message2);
                    $('#inputMessage3').val(campaignData.message3);
                    $('#inputMessage4').val(campaignData.message4);
                    var imageTrimed = campaignData.image_path.replace(/home\/\w+\/\w+\/\w+\//,"");
                    $('#inputImagePathAdmob').val(imageTrimed);
                    $('#inputCampaignNameAdmob').val(campaignData.campaign_name);

                    $('#btnCreateAdmob').val('更新');
                    $(".form-check-input").prop("checked",false);
                    $(".form-check-input").prop("disabled",true);
                    $("#onlyCheckAdmobAutoCreate").prop("checked",true);
                    $("#onlyCheckAdmobAutoCreate").prop("disabled",true);
                    $("#checkAdmobAutoCreate").prop("disabled",true);
                }
            }
        }, "json");
    }
}

//从（暂定）index2.jsp 或 index.jsp传来的参数进行各表单的自动填充
function indexInitFormData(isIndexCreate,campaign_id) {
    if (isIndexCreate) {
        $.post("IndexCampaignCreate", {
            campaign_id:campaign_id
        },function (data) {
            var str = data;   //这里传回的data是一个字符串，首先要转成一个json
            var campaignData = JSON.parse(str);   //将字符串转成json
            if(campaignData.no_data === "no_data"){
                alert("There's no data which campaign_id = "+campaign_id);
            }else{
                if (campaignData.flag == "facebook") {
                    $('#checkFacebook').prop('checked', true); //“Facebook广告”：prop()，设置属性checked为true
                    $('#checkAutoCreate').prop('checked', true);


                    $('#selectApp').val(campaignData.app_name);

                    $('#selectAccount').val(campaignData.account_id);
                    $('#selectAccount').trigger('change');

                    $("#inputCreateCount").val(1);

                    $('#selectRegion').val(campaignData.country_region.split(',')); //这里的val()方法用于设置多个值
                    $('#selectRegion').trigger('change');

                    $('#inputBudget').val(IndexBudget);

                    $('#inputBidding').val(IndexBidding);
                    $('#inputBiddingExplode').prop('checked', campaignData.explode_bidding == 1);

                    $('#inputTitle').val(campaignData.title);
                    $('#inputMessage').val(campaignData.message);

                    $('#inputCampaignName').val(campaignData.campaign_name);

                    $("#selectGender").val(campaignData.gender.split(','));
                    $("#selectGender").trigger('change');
                    $("#inputAge").val(campaignData.age);

                    var imageTrimed = campaignData.image_path.replace(/home\/\w+\/\w+\/\w+\//,"");
                    imageTrimed = imageTrimed.replace(/(\/.+\/)(.*\.\w+)/,"$1");
                    var videoTrimed = campaignData.video_path.replace(/home\/\w+\/\w+\/\w+\//,"");
                    videoTrimed = videoTrimed.replace(/(\/.+\/)(.*\.\w+)/,"$1");
                    $("#inputImagePath").val(imageTrimed);
                    $("#inputVideoPath").val(videoTrimed);

                }else if (campaignData.flag == "admob") {
                    $('#checkAdmob').prop('checked', true);
                    $('#checkAdmob').click();
                    $('#checkAdmobAutoCreate').prop('checked', true);

                    $('#selectAppAdmob').val(campaignData.app_name);
                    $('#selectAccountAdmob').val(campaignData.account_id);
                    $('#selectAccountAdmob').trigger('change');

                    $("#inputCreateCountAdmob").val(1);

                    $('#inputCampaignNameAdmob').val(campaignData.campaign_name);

                    $('#inputBudgetAdmob').val(IndexBudget);
                    $('#inputBiddingAdmob').val(IndexBidding);
                    $('#inputBiddingAdmobExplode').prop('checked', campaignData.explode_bidding == 1); //"分离到系列"

                    $('#inputMessage1').val(campaignData.message1);
                    $('#inputMessage2').val(campaignData.message2);
                    $('#inputMessage3').val(campaignData.message3);
                    $('#inputMessage4').val(campaignData.message4);

                    var imageTrimed = campaignData.image_path.replace(/home\/\w+\/\w+\/\w+\//,"");
                    imageTrimed = imageTrimed.replace(/(\/.+\/)(.*\.\w+)/,"$1");
                    $('#inputImagePathAdmob').val(imageTrimed);

                    if(campaignData.country_region != null && campaignData.country_region != ""){
                        $('#selectRegionAdmob').val(campaignData.country_region.split(',')); //将字符串从指定符号处分割为字符串数组
                        $('#selectRegionAdmob').trigger('change');
                    }

                    if(campaignData.excluded_region != "" && campaignData.excluded_region != null){
                        $('#selectRegionUnselectedAdmob').val(campaignData.excluded_region.split(','));
                        $('#selectRegionUnselectedAdmob').trigger('change');
                    }
                }
            }
        });
    }
}
if(isIndexCreate && campaign_id){
    indexInitFormData(isIndexCreate,campaign_id);
}

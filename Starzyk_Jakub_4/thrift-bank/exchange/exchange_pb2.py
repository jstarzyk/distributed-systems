# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: exchange.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf.internal import enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='exchange.proto',
  package='exchange',
  syntax='proto3',
  serialized_options=None,
  serialized_pb=_b('\n\x0e\x65xchange.proto\x12\x08\x65xchange\"B\n\x0c\x43urrencyRate\x12$\n\x04\x63ode\x18\x01 \x01(\x0e\x32\x16.exchange.CurrencyCode\x12\x0c\n\x04rate\x18\x02 \x01(\x01\"<\n\x13\x45xchangeRateRequest\x12%\n\x05\x63odes\x18\x02 \x03(\x0e\x32\x16.exchange.CurrencyCode\"=\n\x14\x45xchangeRateResponse\x12%\n\x05rates\x18\x01 \x03(\x0b\x32\x16.exchange.CurrencyRate*;\n\x0c\x43urrencyCode\x12\x07\n\x03PLN\x10\x00\x12\x07\n\x03USD\x10\x01\x12\x07\n\x03\x45UR\x10\x02\x12\x07\n\x03GBP\x10\x03\x12\x07\n\x03\x43HF\x10\x04\x32T\n\x08\x45xchange\x12H\n\x0bStreamRates\x12\x1d.exchange.ExchangeRateRequest\x1a\x16.exchange.CurrencyRate\"\x00\x30\x01\x62\x06proto3')
)

_CURRENCYCODE = _descriptor.EnumDescriptor(
  name='CurrencyCode',
  full_name='exchange.CurrencyCode',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='PLN', index=0, number=0,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='USD', index=1, number=1,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='EUR', index=2, number=2,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='GBP', index=3, number=3,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='CHF', index=4, number=4,
      serialized_options=None,
      type=None),
  ],
  containing_type=None,
  serialized_options=None,
  serialized_start=221,
  serialized_end=280,
)
_sym_db.RegisterEnumDescriptor(_CURRENCYCODE)

CurrencyCode = enum_type_wrapper.EnumTypeWrapper(_CURRENCYCODE)
PLN = 0
USD = 1
EUR = 2
GBP = 3
CHF = 4



_CURRENCYRATE = _descriptor.Descriptor(
  name='CurrencyRate',
  full_name='exchange.CurrencyRate',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='code', full_name='exchange.CurrencyRate.code', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='rate', full_name='exchange.CurrencyRate.rate', index=1,
      number=2, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=28,
  serialized_end=94,
)


_EXCHANGERATEREQUEST = _descriptor.Descriptor(
  name='ExchangeRateRequest',
  full_name='exchange.ExchangeRateRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='codes', full_name='exchange.ExchangeRateRequest.codes', index=0,
      number=2, type=14, cpp_type=8, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=96,
  serialized_end=156,
)


_EXCHANGERATERESPONSE = _descriptor.Descriptor(
  name='ExchangeRateResponse',
  full_name='exchange.ExchangeRateResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='rates', full_name='exchange.ExchangeRateResponse.rates', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=158,
  serialized_end=219,
)

_CURRENCYRATE.fields_by_name['code'].enum_type = _CURRENCYCODE
_EXCHANGERATEREQUEST.fields_by_name['codes'].enum_type = _CURRENCYCODE
_EXCHANGERATERESPONSE.fields_by_name['rates'].message_type = _CURRENCYRATE
DESCRIPTOR.message_types_by_name['CurrencyRate'] = _CURRENCYRATE
DESCRIPTOR.message_types_by_name['ExchangeRateRequest'] = _EXCHANGERATEREQUEST
DESCRIPTOR.message_types_by_name['ExchangeRateResponse'] = _EXCHANGERATERESPONSE
DESCRIPTOR.enum_types_by_name['CurrencyCode'] = _CURRENCYCODE
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

CurrencyRate = _reflection.GeneratedProtocolMessageType('CurrencyRate', (_message.Message,), dict(
  DESCRIPTOR = _CURRENCYRATE,
  __module__ = 'exchange_pb2'
  # @@protoc_insertion_point(class_scope:exchange.CurrencyRate)
  ))
_sym_db.RegisterMessage(CurrencyRate)

ExchangeRateRequest = _reflection.GeneratedProtocolMessageType('ExchangeRateRequest', (_message.Message,), dict(
  DESCRIPTOR = _EXCHANGERATEREQUEST,
  __module__ = 'exchange_pb2'
  # @@protoc_insertion_point(class_scope:exchange.ExchangeRateRequest)
  ))
_sym_db.RegisterMessage(ExchangeRateRequest)

ExchangeRateResponse = _reflection.GeneratedProtocolMessageType('ExchangeRateResponse', (_message.Message,), dict(
  DESCRIPTOR = _EXCHANGERATERESPONSE,
  __module__ = 'exchange_pb2'
  # @@protoc_insertion_point(class_scope:exchange.ExchangeRateResponse)
  ))
_sym_db.RegisterMessage(ExchangeRateResponse)



_EXCHANGE = _descriptor.ServiceDescriptor(
  name='Exchange',
  full_name='exchange.Exchange',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=282,
  serialized_end=366,
  methods=[
  _descriptor.MethodDescriptor(
    name='StreamRates',
    full_name='exchange.Exchange.StreamRates',
    index=0,
    containing_service=None,
    input_type=_EXCHANGERATEREQUEST,
    output_type=_CURRENCYRATE,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_EXCHANGE)

DESCRIPTOR.services_by_name['Exchange'] = _EXCHANGE

# @@protoc_insertion_point(module_scope)

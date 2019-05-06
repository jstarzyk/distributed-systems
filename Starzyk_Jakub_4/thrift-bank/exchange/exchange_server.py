import logging
import time
from concurrent import futures
from random import randint

import grpc

import exchange_pb2
import exchange_pb2_grpc

RATES = {
    'PLN': 1,
    'USD': 3.6,
    'EUR': 4,
    'GBP': 5,
    'CHF': 4.5
}

PORT = 50051


class Exchange(exchange_pb2_grpc.ExchangeServicer):

    def StreamRates(self, request, context):
        # print(request.codes)
        print('Received request for:')
        print(request)

        codes = request.codes

        while True:
            val = randint(-100, 100) / 100

            for c in codes:
                res = exchange_pb2.CurrencyRate(
                    code=c,
                    rate=RATES.get(exchange_pb2.CurrencyCode.Name(c)) + val)

                print('Sending...')
                print(res)

                yield res

            time.sleep(5)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    exchange_pb2_grpc.add_ExchangeServicer_to_server(Exchange(), server)

    print('Starting exchange... (' + str(PORT) + ')')

    server.add_insecure_port('[::]:' + str(PORT))
    server.start()

    try:
        while True:
            time.sleep(60)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    logging.basicConfig()
    serve()

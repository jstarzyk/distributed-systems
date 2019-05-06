from concurrent import futures
import time
import logging

import grpc

import exchange_pb2
import exchange_pb2_grpc



class Exchange(exchange_pb2_grpc.ExchangeServicer):

    # rates = e

    def set_rates(self):


    def StreamRates(self, request, context):
        # print(request.codes)
        codes = request.codes

        # print(context)
        # r = [exchange_pb2.CurrencyRate()]
        yield exchange_pb2.CurrencyRate(code=exchange_pb2.EUR, rate=0.21)
        yield exchange_pb2.CurrencyRate(code=exchange_pb2.USD, rate=0.29)
        # return exchange_pb2.
        # return exchange_pb2.CurrencyRate(code=)

    # def SayHello(self, request, context):
    #     return helloworld_pb2.HelloReply(message='Hello, %s!' % request.name)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    exchange_pb2_grpc.add_ExchangeServicer_to_server(Exchange(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            time.sleep(60)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    logging.basicConfig()
    serve()

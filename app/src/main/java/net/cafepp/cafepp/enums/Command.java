package net.cafepp.cafepp.enums;

public enum Command {
  REGISTERED, UNREGISTERED,
  LISTEN,
  FOUND, LOST, CLEAR, REFRESH, PAIRED, NOT_PAIRED,
  INFO_REQ, INFO, CONNECT_CLIENT, CONNECT_SERVER, DISCONNECT_CLIENT, DISCONNECT_SERVER,
  ALLOW_PAIR, REFUSE_PAIR,
  PAIR_REQ, PAIR_ANSWER, PAIR_SERVER_ACCEPT, PAIR_CLIENT_ACCEPT, PAIR_SERVER_DECLINE,
  PAIR_CLIENT_DECLINE, UNPAIR_CLIENT, UNPAIR_SERVER
}
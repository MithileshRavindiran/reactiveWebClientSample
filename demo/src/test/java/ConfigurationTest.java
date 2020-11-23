
@ExtendWith({MockitoExtension.class})
class ConfigurationTest {
@Spy
private WebClient.Builder mockWebClientBuilder = WebClient.builder();
@Mock
private ServerOAuth2AuthorizedClientExchangeFilterFunction mockServerOAuth2AuthorizedClientExchangeFilterFunction;
@Mock
private Connection mockConnection;
@Mock
private TcpClient mockTcpClient;
@Spy
private HttpClient mockHttpClient = HttpClient.create();
@Mock
private ClientSearchServiceConfig mockClientSearchServiceConfig;
@Mock
private ClientResponse mockClientResponse;
@Mock
private HttpStatus mockHttpStatus;
@InjectMocks
private ClientSearchConfiguration fixture;
@Captor
private ArgumentCaptor<Function<? super TcpClient, ? extends TcpClient>> functionArgumentCaptor;
@Captor
private ArgumentCaptor<Consumer<? super Connection>> consumerArgumentCaptor;
@Test
void getHttpClient() {
    assertNotNull(fixture.getHttpClient());
}
@Test
void getClientHttpConnector() {
    // Arrange
    when(mockTcpClient.option(any(), anyInt())).thenReturn(mockTcpClient);
    // Act
    ClientHttpConnector res = fixture.getClientHttpConnector(1,1, false, mockHttpClient);
    // Assert
    assertNotNull(res);
    verify(mockHttpClient).tcpConfiguration(functionArgumentCaptor.capture());
    assertNotNull(functionArgumentCaptor.getValue());
    Function<? super TcpClient, ? extends TcpClient> captured = functionArgumentCaptor.getValue();
    captured.apply(mockTcpClient);
    verify(mockTcpClient).option(ChannelOption.CONNECT_TIMEOUT_MILLIS,1);
    verify(mockTcpClient).doOnConnected(consumerArgumentCaptor.capture());
    consumerArgumentCaptor.getValue().accept(mockConnection);
    verify(mockConnection).addHandlerLast(any());
}
@Test
void getWebClientTest(){
    // Act
    WebClient res = fixture.getWebClient(mockWebClientBuilder, mockHttpClient);
    // Assert
    assertNotNull(res);
    verify(mockClientSearchServiceConfig).getUrl();
    verify(mockClientSearchServiceConfig).getConnectTimeout();
    verify(mockClientSearchServiceConfig).getReadTimeout();
    verify(mockClientSearchServiceConfig).isWiretap();
}
@Test
void getServiceClientNameTest(){
    // Act
    String res = fixture.getServiceClientName();
    // Assert
    assertNotNull(res);
    assertEquals(CLIENT_SEARCH_QUALIFIER, res);
}
@Test
void getServiceRunTimeExceptionTest(){
    // Arrange
    when(mockClientResponse.statusCode()).thenReturn(HttpStatus.OK);
    // Act
    ClientSearchException res = fixture.getServiceRunTimeException(mockClientResponse);
    // Assert
    assertNotNull(res);
    verify(mockClientResponse).statusCode();
}
@Test
void getClientResponseMonoFunction_4xxError_Test(){
    //Arrange
    when(mockClientResponse.statusCode()).thenReturn(mockHttpStatus);
    when(mockHttpStatus.is4xxClientError()).thenReturn(true);
    when(mockHttpStatus.is5xxServerError()).thenReturn(false);
    //Act
    Function<ClientResponse, Mono<ClientResponse>> res = fixture.getClientResponseMonoFunction();
    //Assert
    assertNotNull(res);
    res.apply(mockClientResponse);
    verify(mockClientResponse, times(4)).statusCode();
}
@Test
void getClientResponseMonoFunction_5xxError_Test(){
    //Arrange
    when(mockClientResponse.statusCode()).thenReturn(mockHttpStatus);
    when(mockHttpStatus.is5xxServerError()).thenReturn(true);
    //Act
    Function<ClientResponse, Mono<ClientResponse>> res = fixture.getClientResponseMonoFunction();
    //Assert
    assertNotNull(res);
    res.apply(mockClientResponse);
    verify(mockClientResponse, times(3)).statusCode();
}
@Test
void getClientResponseMonoFunction_Success_Test(){
    //Arrange
    when(mockClientResponse.statusCode()).thenReturn(mockHttpStatus);
    when(mockHttpStatus.is4xxClientError()).thenReturn(false);
    when(mockHttpStatus.is5xxServerError()).thenReturn(false);
    //Act
    Function<ClientResponse, Mono<ClientResponse>> res = fixture.getClientResponseMonoFunction();
    //Assert
    assertNotNull(res);
    res.apply(mockClientResponse);
    verify(mockClientResponse, times(3)).statusCode();
}

}

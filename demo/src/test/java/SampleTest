

@ExtendWith({MockitoExtension.class})
class SampleTest {

@Mock
private EnterpriseClientSearchRsType mockEnterpriseClientSearchRsType;
@Mock
private Mono<EnterpriseClientSearchRsType> mockEnterpriseClientSearchRsTypeMono;
@Mock
private WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;
@Mock
private WebClient.RequestBodyUriSpec mockRequestBodyUriSpec;
@Mock
private WebClient.RequestBodySpec mockRequestBodySpec;
@Mock
private WebClient.RequestHeadersSpec mockRequestHeadersSpec;
@Mock
private WebClient.ResponseSpec mockResponseSpec;
@Mock
private ClientSearchServiceConfig mockConfig;
@Mock
private WebClient mockWebClient;
@Mock
private UserDtoMapper mockUserDtoMapper;
@InjectMocks
private ClientRetrieverService fixture;
@Captor
private ArgumentCaptor<Function<UriBuilder, URI>> functionArgumentCaptor;
@Test
void getListOfClients_byId() {
    // Arrange
    List<DataType> dataTypes = Arrays.asList(
            new DataType()
    );
    UriBuilder mockUriBuilder = Mockito.mock(UriBuilder.class);
    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri((Function<UriBuilder, URI>) any())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.header(anyString(), anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.header(anyString(), anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.accept(any())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(EnterpriseClientSearchRsType.class)).thenReturn(mockEnterpriseClientSearchRsTypeMono);
    when(mockEnterpriseClientSearchRsTypeMono.block()).thenReturn(mockEnterpriseClientSearchRsType);
    when(mockEnterpriseClientSearchRsType.getData()).thenReturn(dataTypes);
    when(mockConfig.isEnabled()).thenReturn(true);
    when(mockConfig.getByIdUri()).thenReturn("/clientId");
    when(mockUriBuilder.path(anyString())).thenReturn(mockUriBuilder);
    when(mockUriBuilder.build(any(), anyString())).thenReturn(null);
    // Act
    List<UserDto> res = fixture.getListOfClients("");
    // Assert
    assertNotNull(res);
    verify(mockRequestHeadersUriSpec).uri(functionArgumentCaptor.capture());
    assertNotNull(functionArgumentCaptor.getValue());
    Function<UriBuilder, URI> captured = functionArgumentCaptor.getValue();
    captured.apply(mockUriBuilder);
    verify(mockUriBuilder).build(any(), anyString());
    verify(mockConfig).getByIdUri();
    
    
    when(mockConfig.isEnabled()).thenReturn(true);
when(mockConfig.getByPersonUri()).thenReturn("http://some.site");
when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodySpec);
when(mockRequestBodySpec.header(anyString(), anyString())).thenReturn(mockRequestBodySpec);
when(mockRequestBodySpec.accept(any())).thenReturn(mockRequestBodySpec);
when(mockRequestBodySpec.body(any(), (Class< EnterpriseClientSearchRqType>) any())).thenReturn(mockRequestHeadersSpec);
when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
when(mockResponseSpec.bodyToMono(EnterpriseClientSearchRsType.class)).thenReturn(mockEnterpriseClientSearchRsTypeMono);
when(mockEnterpriseClientSearchRsTypeMono.block()).thenReturn(mockEnterpriseClientSearchRsType);
when(mockEnterpriseClientSearchRsType.getData()).thenReturn(dataTypes);
}
}

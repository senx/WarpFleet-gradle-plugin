<%
  'inputname' STORE
  '.*[!%25&\\(\\)\\*+/<=>\\[\\]^\\{\\|\\}~].*' MATCHER 'ForbiddenCharacters' STORE
  <% $inputname $ForbiddenCharacters MATCH SIZE 0 >
    //here is the list of exception. '-' will be turned into opb64
    $inputname '-' == ||
    $inputname 'pi' == ||  //name problem for cas insensitive systems
    $inputname 'PI' == ||  //name problem for cas insensitive systems
    $inputname 'e' == ||   //name problem for cas insensitive systems
    $inputname 'E' == ||   //name problem for cas insensitive systems
    $inputname 'Pfilter' == || //name problem for cas insensitive systems
  %>
  <%
    //forbidden character detected, return opb64
    $inputname ->OPB64
  %>
  <%
    //ok, return input name
    $inputname
  %>
  IFTE
%> 'mantranslate' STORE
{{macro}}
{} 'data' STORE
<%
 <% $macro EVAL %> <% %> <% %> TRY DUP
 'sig' GET
  <%
    <%
      <%
        DUP TYPEOF 'SET' ==  <% SET-> LIST-> '|' SWAP JOIN %> IFT
      %> F LMAP
    %> F LMAP
  %> F LMAP
      'sig' PUT $macro '_id' PUT     'data' STORE
    $data 'related' GET 'related' STORE
    [ $related NULL != $related SIZE 0 > ] &&
    <%
      $data $related <% 'label' STORE {
        'label' $label
        'b64' $label @mantranslate
        } %> F LMAP 'related' PUT 'data' STORE
    %>
    <% $data [] 'related' PUT 'data' STORE %>
    IFTE %>
<%  %>
<% $data %> TRY

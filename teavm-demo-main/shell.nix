with import <nixpkgs> { };

mkShell {

  name = "env";
  buildInputs = [
    nodejs python3 quickjs jbang
  ];

  shellHook = ''
    echo TeaVM Demo
  '';

}
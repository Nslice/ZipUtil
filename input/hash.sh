{ export LC_ALL=C;
  du -0ab | sort -z; # file lengths, including directories (with length 0)
  echo | tr '\n' '\000'; # separator
  find -type f -exec sha256sum {} + | sort -z; # file hashes
  echo | tr '\n' '\000'; # separator
  echo "End of hashed data."; # End of input marker
} | sha256sum

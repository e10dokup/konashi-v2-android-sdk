import sys
import os
import StringIO
import xml.etree.ElementTree as ET

def main():
    assert 1 < len(sys.argv)
    path = sys.argv[1]
    tree = None
    with open(path) as f:
        source = ''.join(f.readlines()[1:]) + '</html>'
        print source
        html = StringIO.StringIO(source)
        tree = ET.parse(html)
    if not tree:
        print('Failed to load html')
        return
    root = tree.getroot()
    contents = root.find('body').find('div')

    summary_group = (contents.findall('div')[0]  # #summary
                     .find('table').find('tr').find('td').find('div'))  # #summaryGroup

    summary = dict([(td.find('div').find('p').text,
                     td.find('div').find('div').text)
                    for td in summary_group.find('table').find('tr').findall('td')])
    print('=' * 80)
    print(' | '.join(['{}: {}'.format(k, v) for k, v in summary.items()]))
    print('=' * 80)

    if int(summary['failures']) == 0:
        return

    print('Failures')
    print('-' * 20)
    class_tr_list = (contents
                     .findall('div')[1]  # #tabs
                     .findall('div')[0]  # #tab0
                     .find('ul').find('table').findall('tr'))
    for tr in class_tr_list:
        method = '#'.join([td.find('a').text for td in tr.findall('td')])
        print(method)

if __name__ == '__main__':
    main()
